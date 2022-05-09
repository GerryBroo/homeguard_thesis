package hu.geribruu.homeguardbeta.feature.face_detection.presentation.face_gallery_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.feature.face_detection.data.repository.FaceRepositoryImpl
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceGalleryViewModel @Inject constructor(
    private val repository: FaceRepositoryImpl
) : ViewModel() {

    val faces: LiveData<List<RecognizedFace>> = repository.getFaces().asLiveData()

    fun deleteFace(face: RecognizedFace) {
        viewModelScope.launch {
            repository.deleteFace(face)
        }
    }
}