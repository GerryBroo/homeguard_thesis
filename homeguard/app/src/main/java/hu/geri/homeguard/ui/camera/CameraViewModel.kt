package hu.geri.homeguard.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.camera.CameraUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CameraViewModel(private val cameraUseCases: CameraUseCases) : ViewModel() {

    private var getRecognizedObjectJob: Job? = null
    private var getRecognizedFaceJob: Job? = null

    private val _regonizedObjectText = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val recognizedObjectText: LiveData<String> = _regonizedObjectText

    private val _regonizedFaceText = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val recognizedFaceText: LiveData<String> = _regonizedFaceText

    private fun getRecognizedFace() {
        getRecognizedFaceJob?.cancel()
        getRecognizedFaceJob = cameraUseCases.getRecognizedFaceUseCase()
            .onEach { recognizedFace ->
                _regonizedObjectText.apply {
                    value = recognizedFace
                }
            }.launchIn(viewModelScope)
    }

    private fun getRecognizedObject() {
        getRecognizedObjectJob?.cancel()
        getRecognizedObjectJob = cameraUseCases.getRecognizedObjectUseCase()
            .onEach { recognizedObject ->
                _regonizedObjectText.apply {
                    value = recognizedObject
                }
            }.launchIn(viewModelScope)
    }

    // IMPORTANT TO BE ON THE BOTTOM, BECAUSE OF THE INIT FLOW
    init {
        getRecognizedFace()
        getRecognizedObject()
    }
}