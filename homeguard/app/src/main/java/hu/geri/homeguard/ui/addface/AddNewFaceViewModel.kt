package hu.geri.homeguard.ui.addface

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.analyzer.Azigen
import hu.geri.homeguard.domain.camera.usecase.CameraUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddNewFaceViewModel(private val cameraUseCases: CameraUseCases) : ViewModel() {

    private var getRecognizedFaceJob: Job? = null
    private var getFacePreviewJob: Job? = null

    private val _regonizedFaceText = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val recognizedFaceText: LiveData<String> = _regonizedFaceText

    private val _facePreview = MutableLiveData<Azigen>().apply {
        value = null
    }
    val facePreview: LiveData<Azigen> = _facePreview

    private fun getRecognizedFace() {
        getRecognizedFaceJob?.cancel()
        getRecognizedFaceJob = cameraUseCases.getRecognizedFaceUseCase()
            .onEach { recognizedFace ->
                _regonizedFaceText.apply {
                    value = recognizedFace
                }
            }.launchIn(viewModelScope)
    }

    private fun getFacePreview() {
        getFacePreviewJob?.cancel()
        getFacePreviewJob = cameraUseCases.getPreviewUseCase()
            .onEach { preView ->
                _facePreview.apply {
                    value = preView
                }
            }.launchIn(viewModelScope)
    }

    fun setNewFace(name: String) {
        cameraUseCases.addFaceUseCase(name)
    }

    // IMPORTANT TO BE ON THE BOTTOM, BECAUSE OF THE INIT FLOW
    init {
        getRecognizedFace()
        getFacePreview()
    }
}
