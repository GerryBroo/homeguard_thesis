package hu.geri.homeguard.domain.camera

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val owner: LifecycleOwner,
    private val context: Context,
    private val viewPreview: PreviewView
) {
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var lensFacing: Int = LENS_FACING_FRONT
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera(onFrontCamera: Boolean?) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            controlWhichCameraToDisplay(frontCamera = onFrontCamera)
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases() {
        cameraProvider?.let { cameraProvider ->
            val cameraSelector = getCameraSelector()
            val previewView = getPreviewUseCase()
            cameraProvider.unbindAll()
            try {
                camera = cameraProvider.bindToLifecycle(
                    owner,
                    cameraSelector,
                    previewView
                )

                previewView.setSurfaceProvider(viewPreview.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed $exc")
            }
        } ?: throw IllegalStateException("Camera initialization failed.")
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

    private fun controlWhichCameraToDisplay(frontCamera: Boolean?): Int {
        lensFacing = when (frontCamera) {
            true -> LENS_FACING_FRONT
            else -> LENS_FACING_BACK
        }
        return lensFacing
    }

    fun stop() {
        cameraExecutor.shutdown()
    }

    companion object {
        const val LENS_FACING_FRONT = CameraSelector.LENS_FACING_FRONT
        const val LENS_FACING_BACK= CameraSelector.LENS_FACING_BACK
    }
}