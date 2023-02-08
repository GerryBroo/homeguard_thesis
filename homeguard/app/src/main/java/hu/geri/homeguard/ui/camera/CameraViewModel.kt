package hu.geri.homeguard.ui.camera

import android.util.Log
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

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private fun getRecognizedObject() {
        getRecognizedObjectJob?.cancel()
        getRecognizedObjectJob = cameraUseCases.getRecognizedObjectUseCase()
            .onEach { notes ->
                Log.d("asd", "viewmodel Stateflow ${notes}")
                _text.apply {
                    value = notes
                }
            }.launchIn(viewModelScope)
    }

    // IMPORTANT TO BE ON THE BOTTOM, BECAUSE OF THE INIT FLOW
    init {
        getRecognizedObject()
    }
}