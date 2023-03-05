package hu.geri.homeguard.ui.facegallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.face.FaceUseCase
import hu.geri.homeguard.domain.face.FacesEmptyError
import hu.geri.homeguard.domain.face.FacesSuccess
import hu.geri.homeguard.domain.face.model.RecognizedFace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FaceGalleryViewModel(
    private val faceUseCase: FaceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceGalleryUiState(isLoading = true))
    val uiState: StateFlow<FaceGalleryUiState> = _uiState

    private val _faces = MutableLiveData<List<RecognizedFace>>().apply {
        value = emptyList()
    }
    val face: LiveData<List<RecognizedFace>> = _faces

    // TODO fix UI STATE https://medium.com/android-news/architecture-components-easy-mapping-of-actions-and-ui-state-207663e3fdd

    fun loadFaces() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = faceUseCase.getFaces()) {
                is FacesSuccess -> {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false, faces = result.face)
                    }
                }
                is FacesEmptyError -> {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false, errorMessage = "Empty list")
                    }
                }
            }
        }
    }

    fun deleteFace(face: RecognizedFace) {
        viewModelScope.launch(Dispatchers.IO) {
            faceUseCase.deleteFace(face)
        }
    }
}

data class FaceGalleryUiState(
    val faces: List<RecognizedFace> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)