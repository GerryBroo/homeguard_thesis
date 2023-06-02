package hu.geri.homeguard.ui.addface

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.analyzer.model.AddFaceData
import hu.geri.homeguard.domain.camera.usecase.CameraUseCases
import hu.geri.homeguard.domain.face.FaceUseCase
import hu.geri.homeguard.domain.face.util.deleteFaceImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AddFaceViewModel(
    private val cameraUseCases: CameraUseCases,
    private val faceUseCase: FaceUseCase
) : ViewModel() {

    private var getRecognizedFaceJob: Job? = null
    private val _regonizedFaceText = MutableLiveData<String>().apply {
        value = "No face detected"
    }
    val recognizedFaceText: LiveData<String> = _regonizedFaceText

    private lateinit var newFaceData: AddFaceData

    private fun getRecognizedFace() {
        getRecognizedFaceJob?.cancel()
        getRecognizedFaceJob = cameraUseCases.getRecognizedFaceUseCase()
            .onEach { recognizedFace ->
                _regonizedFaceText.apply {
                    value = recognizedFace
                }
            }.launchIn(viewModelScope)
    }

    fun getPreview(): Bitmap? {
        viewModelScope.launch {
            val faceData = cameraUseCases.getPreviewUseCase()
            newFaceData = AddFaceData(
                faceData.bitmap,
                faceData.embeedings,
                faceData.url,
                faceData.type
            )
        }
        return newFaceData.bitmap
    }

    fun setNewFace(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            newFaceData.embeedings?.let { faceUseCase.saveFace(name, it, newFaceData.url) }
        }
    }

    fun cancelFaceImage() {
        deleteFaceImage(newFaceData.url)
    }

    // IMPORTANT TO BE ON THE BOTTOM, BECAUSE OF THE INIT FLOW
    init {
        getRecognizedFace()
    }
}
