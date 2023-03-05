package hu.geri.homeguard.ui.facegallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.face.FaceUseCase
import hu.geri.homeguard.domain.face.FacesEmptyError
import hu.geri.homeguard.domain.face.FacesSuccess
import hu.geri.homeguard.domain.face.model.RecognizedFace
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

    fun loadFaces() {
        viewModelScope.launch {
            when (val result = faceUseCase.getFaces()) {
                is FacesSuccess -> {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(faces = result.face)
                    }
                }
                is FacesEmptyError -> {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(errorMessage = "Empty list")
                    }
                }
            }
        }
    }
}

data class FaceGalleryUiState(
    val faces: List<RecognizedFace> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)