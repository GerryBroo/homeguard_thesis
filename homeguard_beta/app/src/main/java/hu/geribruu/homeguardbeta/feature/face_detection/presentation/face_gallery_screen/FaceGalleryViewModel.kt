package hu.geribruu.homeguardbeta.feature.face_detection.presentation.face_gallery_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FaceGalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Face Gallery Fragment"
    }
    val text: LiveData<String> = _text
}