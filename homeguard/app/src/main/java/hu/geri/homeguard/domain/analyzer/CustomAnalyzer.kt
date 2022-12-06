package hu.geri.homeguard.domain.analyzer

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage

class CustomAnalyzer: ImageAnalysis.Analyzer {

    private val objectDetector = customObjectDetector("model.tflite")

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            objectDetector.process(processImage)
                .addOnSuccessListener { objects ->
                    for (detectedObject in objects) {
                        val name = detectedObject.labels.firstOrNull()?.text ?: "Undefined"
                        Log.d("ASD", name)
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