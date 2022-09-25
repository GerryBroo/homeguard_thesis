package hu.geribruu.homeguardbeta.ui.cameraScreen

import android.content.Context
import android.widget.TextView
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.domain.faceRecognition.CameraManager
import javax.inject.Inject

@HiltViewModel
class CameraPreviewViewModel @Inject constructor() : ViewModel() {

    lateinit var cameraManager: CameraManager

    fun setCamera(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        viewPreview: PreviewView,
        recognInfo: TextView,
    ) {
        cameraManager = CameraManager(
            owner = lifecycleOwner,
            context = context,
            viewPreview = viewPreview,
            recognitionInfo = recognInfo,
        )
    }

    fun startCamera(onFrontCamera: Boolean = true) {
        cameraManager.startCamera(onFrontCamera = onFrontCamera)
    }

    fun stopCamera() {
        cameraManager.stop()
    }
}
