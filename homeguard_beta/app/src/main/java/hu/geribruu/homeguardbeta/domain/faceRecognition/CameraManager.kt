package hu.geribruu.homeguardbeta.domain.faceRecognition

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import android.util.Pair
import android.util.Size
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.Tasks
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.EntryPointAccessors
import hu.geribruu.homeguardbeta.HomaGuardApp
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.SimilarityClassifier
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.getCropBitmapByCPU
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.getResizedBitmap
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.readFromSP
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.rotateBitmap
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.toBitmap
import hu.geribruu.homeguardbeta.ui.MainActivity
import hu.geribruu.homeguardbeta.ui.addNewFaceScreen.ExistingFace
import hu.geribruu.homeguardbeta.ui.addNewFaceScreen.FaceState
import hu.geribruu.homeguardbeta.ui.addNewFaceScreen.NoFace
import hu.geribruu.homeguardbeta.ui.addNewFaceScreen.OkFace
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class CameraManager @Inject constructor(
    private val owner: LifecycleOwner,
    private val context: Context,
    private val viewPreview: PreviewView,
    private val recognitionInfo: TextView,
    private val facePreview: ImageView? = null,
) {

    private val faceManager =
        EntryPointAccessors.fromApplication(context, HomaGuardApp.InitializerEntryPoint::class.java)
            .faceCaptureManager()
    private val imageCapture =
        EntryPointAccessors.fromApplication(context, HomaGuardApp.InitializerEntryPoint::class.java)
            .imageCapture()

    private val faceDetectorOption = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    val faceDetector = FaceDetection.getClient(faceDetectorOption)

    val objectDetector = CustomObjectDetector("bird_detection.tflite").objectDetector

    var flipX = false // todo ey is fontos
    var isModelQuantized = false // todo constans
    lateinit var intValues: IntArray // todo nem v'gom miert lateniat
    lateinit var embeedings: Array<FloatArray> // todo szinten nem vagom miert lateinit

    var inputSize = 112 // Input size for model

    var IMAGE_MEAN = 128.0f
    var IMAGE_STD = 128.0f
    var OUTPUT_SIZE = 192 // Output size of model

    var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>() // saved Faces

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private lateinit var cameraProvider: ProcessCameraProvider

    fun stop() {
        cameraExecutor.shutdown()
    }

    private fun controlWhichCameraToDisplay(frontCamera: Boolean?): Int {
        lensFacing = when (frontCamera) {
            true -> CameraSelector.LENS_FACING_FRONT
            else -> CameraSelector.LENS_FACING_BACK
        }
        return lensFacing
    }

    fun startCamera(onFrontCamera: Boolean?) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            controlWhichCameraToDisplay(frontCamera = onFrontCamera)
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(context))

        registered = readFromSP(context!!)
    }

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var previewView: PreviewView? = null

    private fun bindCameraUseCases() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context!!)
        previewView = viewPreview
        cameraProviderFuture!!.addListener({
            try {
                cameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(context!!))
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Latest frame is shown
            .build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor) { imageProxy ->
            try {
                Thread.sleep(0) // Camera preview refreshed every 10 millisec(adjust as required)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            var image: InputImage? = null
            @SuppressLint("UnsafeExperimentalUsageError") var mediaImage = imageProxy.image
                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
            if (mediaImage != null) {
                image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            }

            objectDetector.process(image!!)
                .addOnSuccessListener { objects ->

                    for (detectedObject in objects) {

                        val objectName = detectedObject.labels.firstOrNull()?.text ?: "Undefined"
                        Log.d("asd", "detetction: $objectName")
                    }
                }
                .addOnFailureListener {
                    Log.v("ImageAnalyzer", "Error - ${it.message}")
                }
                .addOnCompleteListener {

                    mediaImage = imageProxy.image

                    val planes = mediaImage!!.planes
                    if (planes.size >= 3) {
                        // Reset buffer position for each plane's buffer.
                        for (plane in planes) {
                            plane.buffer.rewind()
                        }
                    }

                    // Process acquired image to detect faces
                    faceDetector.process(image)
                        .addOnSuccessListener { faces ->
                            if (faces.size != 0) {
                                val face = faces[0] // Get first face from detected faces

                                // mediaImage to Bitmap
                                val frame_bmp = toBitmap(mediaImage)
                                val rot = imageProxy.imageInfo.rotationDegrees

                                // Adjust orientation of Face
                                val frame_bmp1 =
                                    rotateBitmap(frame_bmp, rot, false, false)

                                // Get bounding box of face
                                val boundingBox = RectF(face.boundingBox)

                                // Crop out bounding box from whole Bitmap(image)
                                var cropped_face =
                                    getCropBitmapByCPU(frame_bmp1, boundingBox)
                                if (flipX) cropped_face =
                                    rotateBitmap(cropped_face, 0, flipX, false)
                                // Scale the acquired Face to 112*112 which is required input for model
                                val scaled = getResizedBitmap(cropped_face, 112, 112)
                                recognizeImage(scaled) // Send scaled bitmap to create face embeddings.
                            } else {
                                recognitionInfo.text = "No Face Detected!"
                            }
                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                        }
                        .addOnCompleteListener {
                            imageProxy.close() // v.important to acquire next frame for analysis
                        }
                }
        }

        val objectImageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        objectImageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            ObjectImageAnalyzer()
        )

        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            owner,
            getCameraSelector(),
            preview,
            imageAnalysis,
            imageCapture
        )
    }

    private fun getCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    private fun getPreviewUseCase(): Preview {
        return Preview.Builder()
            .setTargetRotation(viewPreview.display.rotation)
            .build()
    }

    fun isNewFaceAvailable(): FaceState {
        return when (recognitionInfo.text.toString()) {
            "Unknown" -> OkFace
            "" -> OkFace
            "No Face Detected!" -> NoFace
            else -> ExistingFace
        }
    }

    fun setNewFace(name: String) {

        val result = SimilarityClassifier.Recognition(
            "0", "", -1f
        )

        result.extra = embeedings
        registered[name] = result

        faceManager.manageNewFace(registered, name)
    }

    fun recognizeImage(bitmap: Bitmap) {

        // set Face to Preview
        facePreview?.setImageBitmap(bitmap)

        // Create ByteBuffer to store normalized image
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        intValues = IntArray(inputSize * inputSize)

        // get pixel values from Bitmap to normalize
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
        // imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)
        val outputMap: MutableMap<Int, Any> = HashMap()
        embeedings =
            Array(1) { FloatArray(OUTPUT_SIZE) } // output of model will be stored in this variable
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
                    recognitionInfo.text = name
                } else {
                    recognitionInfo.text = "Unknown"
                }
            }
        }

        faceManager.manageFace(recognitionInfo.text.toString())
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
}
