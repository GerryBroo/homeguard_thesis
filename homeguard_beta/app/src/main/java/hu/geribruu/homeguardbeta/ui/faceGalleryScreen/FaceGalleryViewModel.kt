package hu.geribruu.homeguardbeta.ui.faceGalleryScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceGalleryViewModel @Inject constructor(
    private val repository: FaceDiskDataSource
) : ViewModel() {

    val faces: LiveData<List<RecognizedFace>> = repository.getFaces().asLiveData()

    fun deleteFace(face: RecognizedFace) {
        viewModelScope.launch {
            repository.deleteFace(face)
        }
    }
}