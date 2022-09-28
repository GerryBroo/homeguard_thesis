package hu.geribruu.homeguardbeta.domain.faceRecognition

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage

class ObjectImageAnalyzer(
) : ImageAnalysis.Analyzer {

    private val birdObjectDetector = CustomObjectDetector("bird_detection.tflite").objectDetector

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        val mediaImage = imageProxy.image

        if(mediaImage != null) {
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            birdObjectDetector.process(processImage)
                .addOnSuccessListener { objects ->

                    for (detectedObject in objects) {

                        val name = detectedObject.labels.firstOrNull()?.text ?: "Undefined"
//                        val date = SimpleDateFormat(PhotoCapture.FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
//                        val url = photoCapture.takePhoto()
//
//                        binding.tvCameraFragment.text = name
//
//                        cameraVM.insert(BirdDatabaseModel(0, name, date, url))

                        Log.d("asd","detetction: $name")
                    }
                }
                .addOnFailureListener {
                    Log.v("ImageAnalyzer", "Error - ${it.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}