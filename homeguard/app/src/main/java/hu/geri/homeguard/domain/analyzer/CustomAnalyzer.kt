package hu.geri.homeguard.domain.analyzer

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import hu.geri.homeguard.domain.analyzer.util.customObjectDetector
import kotlinx.coroutines.flow.MutableStateFlow

class CustomAnalyzer(
) : ImageAnalysis.Analyzer {

    private val objectDetector = customObjectDetector("bird_detection.tflite")

    val recognizedObjectStateFlow = MutableStateFlow("Undefined")

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            objectDetector.process(processImage)
                .addOnSuccessListener { objects ->
                    for (detectedObject in objects) {
                        recognizedObjectStateFlow.value =
                            detectedObject.labels.firstOrNull()?.text ?: "Undefined"
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