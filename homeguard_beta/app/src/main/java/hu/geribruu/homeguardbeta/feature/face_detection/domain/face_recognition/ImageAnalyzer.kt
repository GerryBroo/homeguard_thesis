package hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.getCropBitmapByCPU
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.getResizedBitmap
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.rotateBitmap
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.util.toBitmap
import hu.geribruu.homeguardbeta.ui.MainActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.inject.Inject

class ImageAnalyzer @Inject constructor(
    private var context: Context,
    private var faceDetector: FaceDetector,
    private val captureManager : CaptureManager
    ) : ImageAnalysis.Analyzer {

    var flipX = false // todo ey is fontos
    var start = true  // todo kell
    var isModelQuantized = false // todo constans
    lateinit var intValues: IntArray  // todo nem v'gom miert lateniat
    lateinit var embeedings: Array<FloatArray> // todo szinten nem vagom miert lateinit

    var inputSize = 112 //Input size for model

    var IMAGE_MEAN = 128.0f
    var IMAGE_STD = 128.0f
    var OUTPUT_SIZE = 192 //Output size of model

    //    lateinit var reco_name: TextView
    var reco_name: String = ""

    private var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>() //saved Faces

    var previewBitmap: Bitmap? = null

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        try {
            Thread.sleep(0) //Camera preview refreshed every 10 millisec(adjust as required)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        var image: InputImage? = null
        @SuppressLint("UnsafeExperimentalUsageError") val mediaImage// Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
                = imageProxy.image
        if (mediaImage != null) {
            image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            //                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
        }

        //                System.out.println("ANALYSIS");

        //Process acquired image to detect faces
        val result = faceDetector!!.process(image)
            .addOnSuccessListener { faces ->

                if (faces.size != 0) {
                    val face = faces[0] //Get first face from detected faces
                    //                                                    System.out.println(face);

                    //mediaImage to Bitmap
                    val frame_bmp = toBitmap(mediaImage)
                    val rot = imageProxy.imageInfo.rotationDegrees

                    //Adjust orientation of Face
                    val frame_bmp1 =
                        rotateBitmap(frame_bmp, rot, false, false)


                    //Get bounding box of face
                    val boundingBox = RectF(face.boundingBox)

                    //Crop out bounding box from whole Bitmap(image)
                    var cropped_face =
                        getCropBitmapByCPU(frame_bmp1, boundingBox)
                    if (flipX) cropped_face =
                        rotateBitmap(cropped_face, 0, flipX, false)
                    //Scale the acquired Face to 112*112 which is required input for model
                    val scaled = getResizedBitmap(cropped_face, 112, 112)
                    if (start) {
                        recognizeImage(scaled)
                    } //Send scaled bitmap to create face embeddings.
                    //                                                    System.out.println(boundingBox);
                } else {
                    if (registered.isEmpty()) reco_name =
                        "Add Face" else reco_name =
                        "No Face Detected!"
                }
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
            .addOnCompleteListener {
                imageProxy.close() //v.important to acquire next frame for analysis
            }
    }

    fun recognizeImage(bitmap: Bitmap) {

        // set Face to Preview
//        face_preview!!.setImageBitmap(bitmap)

        previewBitmap = bitmap
        captureManager.manageNewFace(bitmap)

        if(previewBitmap == null) {
            Log.d("ASD", "Ay image analzyerben is Null")
        }

        //Create ByteBuffer to store normalized image
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        intValues = IntArray(inputSize * inputSize)

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
                    imgData.put((pixelValue and 0xFF).toByte())
                } else { // Float model
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }
        //imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)
        val outputMap: MutableMap<Int, Any> = HashMap()
        embeedings =
            Array(1) { FloatArray(OUTPUT_SIZE) } //output of model will be stored in this variable
        outputMap[0] = embeedings
        MainActivity.tfLite.runForMultipleInputsOutputs(inputArray, outputMap) //Run model
        var distance_local = Float.MAX_VALUE
        val id = "0"
        val label = "?"

        //Compare new face with saved Faces.
        if (registered.size > 0) {
            val nearest = findNearest(embeedings[0]) //Find 2 closest matching face
            if (nearest[0] != null) {
                val name = nearest[0]!!.first //get name and distance of closest matching face
                // label = name;
                distance_local = nearest[0]!!.second

                if (distance_local < 1.0f) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    reco_name = name else reco_name = "Unknown"
                //                    System.out.println("nearest: " + name + " - distance: " + distance_local);

            }
        }
    }

    //Compare Faces by distance between face embeddings
    private fun findNearest(emb: FloatArray): List<Pair<String, Float>?> {
        val neighbour_list: MutableList<Pair<String, Float>?> = ArrayList()
        var ret: Pair<String, Float>? = null //to get closest match
        var prev_ret: Pair<String, Float>? = null //to get second closest match
        for ((name, value) in registered) {
            val knownEmb: FloatArray = ((value.extra) as Array<*>)[0] as FloatArray
            var distance = 0f
            for (i in emb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = Math.sqrt(distance.toDouble()).toFloat()
            if (ret == null || distance < ret.second) {
                prev_ret = ret
                ret = Pair(name, distance)
            }
        }
        if (prev_ret == null) prev_ret = ret
        neighbour_list.add(ret)
        neighbour_list.add(prev_ret)
        return neighbour_list
    }

    private fun showToastName(str: String) {
        val mToastToShow = Toast.makeText(
            context,
            str,
            Toast.LENGTH_SHORT
        )
        mToastToShow.show()
        val handler = Handler()
        handler.postDelayed(Runnable { mToastToShow.cancel() }, 100)
    }
}