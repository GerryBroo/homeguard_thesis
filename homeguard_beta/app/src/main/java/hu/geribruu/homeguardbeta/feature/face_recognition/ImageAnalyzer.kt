package hu.geribruu.homeguardbeta.feature.face_recognition

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import javax.inject.Inject

class ImageAnalyzer @Inject constructor(
    private var context: Context,
    private var faceDetector: FaceDetector
    ) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val processImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            faceDetector.process(processImage)
                .addOnSuccessListener { faces ->
                    showToastName("Face size: ${faces.size}")

                }
                .addOnFailureListener {
                    Log.v("MainActivity", "Error - ${it.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
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