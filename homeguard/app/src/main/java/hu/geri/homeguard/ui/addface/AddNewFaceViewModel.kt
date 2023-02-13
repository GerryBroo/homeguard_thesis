package hu.geri.homeguard.ui.addface

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.camera.usecase.CameraUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddNewFaceViewModel(private val cameraUseCases: CameraUseCases) : ViewModel() {

    private var getRecognizedFaceJob: Job? = null

    private val _regonizedFaceText = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val recognizedFaceText: LiveData<String> = _regonizedFaceText

    private fun getRecognizedFace() {
        getRecognizedFaceJob?.cancel()
        getRecognizedFaceJob = cameraUseCases.getRecognizedFaceUseCase()
            .onEach { recognizedFace ->
                _regonizedFaceText.apply {
                    value = recognizedFace
                }
            }.launchIn(viewModelScope)
    }

    // IMPORTANT TO BE ON THE BOTTOM, BECAUSE OF THE INIT FLOW
    init {
        getRecognizedFace()
    }
}
