package hu.geribruu.homeguardbeta.ui.cameraScreen

import android.content.Context
import android.widget.TextView
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.domain.faceRecognition.CameraManager
import javax.inject.Inject

@HiltViewModel
class CameraPreviewViewModel @Inject constructor() : ViewModel() {

    lateinit var cameraManager: CameraManager

    val viewState = MutableLiveData<CameraViewState>()

//    init {
//        cameraManager.observeChanges { string ->
//            viewState.value = CameraViewState.CameraLoaded(string)
//        }
//    }

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

    private val observer: (String) -> Unit = { str ->
    }

    fun startCamera(onFrontCamera: Boolean = true) {
        cameraManager.startCamera(onFrontCamera = onFrontCamera)

        cameraManager.observeChanges { string ->
            viewState.value = CameraViewState.CameraLoaded(string)
        }

        cameraManager.observeChanges(observer)
    }

    fun stopCamera() {
        cameraManager.stop()
    }

    sealed class CameraViewState {
        data class CameraLoaded(val str: String) : CameraViewState()
        data class CameraLoadFailed(val str: String) : CameraViewState()
    }
}
