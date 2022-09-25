package hu.geribruu.homeguardbeta.ui.addNewFaceScreen

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.domain.faceRecognition.CameraManager
import javax.inject.Inject

@HiltViewModel
class AddNewFaceViewModel @Inject constructor() : ViewModel() {

    lateinit var cameraManager: CameraManager

    fun setCamera(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        viewPreview: PreviewView,
        recognInfo: TextView,
        facePreview: ImageView? = null,
    ) {
        cameraManager = CameraManager(
            owner = lifecycleOwner,
            context = context,
            viewPreview = viewPreview,
            recognitionInfo = recognInfo,
            facePreview = facePreview
        )
    }

    fun startCamera(onFrontCamera: Boolean = true) {
        cameraManager.startCamera(onFrontCamera = onFrontCamera)
    }

    fun isNewFaceAvailable(): FaceState {
        return cameraManager.isNewFaceAvailable()
    }

    fun setNewFace(name: String) {
        cameraManager.setNewFace(name)
    }

    fun stopCamera() {
        cameraManager.stop()
    }
}
