package hu.geri.homeguard.domain.analyzer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.RectF
import android.media.Image
import android.util.Log
import android.util.Pair
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import hu.geri.homeguard.MainActivity
import hu.geri.homeguard.domain.analyzer.model.AddFaceData
import hu.geri.homeguard.domain.analyzer.model.SimilarityClassifier
import hu.geri.homeguard.domain.analyzer.util.*
import hu.geri.homeguard.domain.camera.PhotoCapture
import kotlinx.coroutines.flow.MutableStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CustomAnalyzer(
    private val photoCapture: PhotoCapture
) : ImageAnalysis.Analyzer {

    private val objectDetector = customObjectDetector("bird_detection.tflite")
    private val faceDetector = customFaceDetector()

    val recognizedObject = MutableStateFlow("Undefined")
    val recognizedFace = MutableStateFlow("Undefined")

    var addFaceData: AddFaceData? = null

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            // TODO REFACTOR
            objectDetector.process(processImage)
                .addOnSuccessListener { objects ->
                    processObjects(objects)
                }
                .addOnFailureListener {
                    Log.v("ImageAnalyzer", "Error - ${it.message}")
                }
                .addOnCompleteListener {

                    // TODO I don't know i need this
                    val planes = mediaImage.planes
                    if (planes.size >= 3) {
                        // Reset buffer position for each plane's buffer.
                        for (plane in planes) {
                            plane.buffer.rewind()
                        }
                    }

                    faceDetector.process(processImage)
                        .addOnSuccessListener { faces ->
                            processFaces(mediaImage, rotationDegrees, faces)
                        }
                        .addOnFailureListener {
                            Log.v("ImageAnalyzer", "Error - ${it.message}")
                        }.addOnCompleteListener {
                            imageProxy.close()
                        }
                }
        }
    }

    private fun processObjects(objects: List<DetectedObject>) {
        for (detectedObject in objects) {
            recognizedObject.value = detectedObject.labels.firstOrNull()?.text ?: "Undefined"
        }
    }

    private fun processFaces(mediaImage: Image, rotationDegree: Int, faces: List<Face>) {
        if (faces.isNotEmpty()) {
            val frameBMP = toBitmap(mediaImage)
            val rotatedFace = rotateBitmap(frameBMP, rotationDegree)

            val face = faces[0]
            val boundingBox = RectF(face.boundingBox)

            val croppedFace = getCropBitmapByCPU(rotatedFace, boundingBox)
            val scaledFace = getResizedBitmap(croppedFace, CROPPED_BITMAP_SIZE, CROPPED_BITMAP_SIZE)

            recognizeImage(scaledFace)
        } else {
            recognizedFace.value = "No face detected!"
        }
    }

    suspend fun setNewFace(name: String, emb: Array<FloatArray>) {

        val result = SimilarityClassifier.Recognition(
            "0", "", -1f
        )

        result.extra = emb
        registered[name] = result
    }

    var isModelQuantized = false // todo constans
    lateinit var embeedings: Array<FloatArray> // todo szinten nem vagom miert lateinit
    var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>() // saved Faces
    lateinit var addFaceBitmap : Bitmap

    // TODO rework because its a spaghetti
    private fun recognizeImage(bitmap: Bitmap) {

        // Create ByteBuffer to store normalized image
        val imgData =
            ByteBuffer.allocateDirect(1 * CROPPED_BITMAP_SIZE * CROPPED_BITMAP_SIZE * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(CROPPED_BITMAP_SIZE * CROPPED_BITMAP_SIZE)

        // get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until CROPPED_BITMAP_SIZE) {
            for (j in 0 until CROPPED_BITMAP_SIZE) {
                val pixelValue = intValues[i * CROPPED_BITMAP_SIZE + j]
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
        // imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)
        val outputMap: MutableMap<Int, Any> = HashMap()
        // output of model will be stored in this variable
        embeedings = Array(1) { FloatArray(OUTPUT_SIZE) }
        outputMap[0] = embeedings
        MainActivity.tfLiteFace.runForMultipleInputsOutputs(inputArray, outputMap) // Run model

        var distance_local = Float.MAX_VALUE
        val id = "0"
        val label = "?"

        // Compare new face with saved Faces.
        if (registered.size > 0) {
            val nearest = findNearest(embeedings[0]) // Find 2 closest matching face
            if (nearest[0] != null) {
                val name = nearest[0]!!.first // get name and distance of closest matching face
                distance_local = nearest[0]!!.second

                if (distance_local < 1.0f) {
                    // If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                    recognizedFace.value = name
                } else {
                }
            }
        } else {
            recognizedFace.value = "Unknown"

        }

        // Set the information to add face dialog
        //addFaceData = AddFaceData(bitmap, embeedings, photoCapture.takePhoto())
        addFaceBitmap = bitmap

//        faceManager.manageFace(recognitionInfo.text.toString())
    }

    fun newFaceEvent(): AddFaceData {
        return AddFaceData(addFaceBitmap, embeedings, photoCapture.takePhoto())
    }

    // Compare Faces by distance between face embeddings
    private fun findNearest(emb: FloatArray): List<Pair<String, Float>?> {
        val neighbour_list: MutableList<Pair<String, Float>?> = ArrayList()
        var ret: Pair<String, Float>? = null // to get closest match
        var prev_ret: Pair<String, Float>? = null // to get second closest match
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

    companion object {
        const val CROPPED_BITMAP_SIZE = 112
        const val IMAGE_MEAN = 128.0f
        const val IMAGE_STD = 128.0f
        const val OUTPUT_SIZE = 192
    }
}