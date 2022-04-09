package hu.geribruu.homeguardbeta.ui.camera_preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraPreviewViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is CameraPreview Fragment"
    }
    val text: LiveData<String> = _text
}