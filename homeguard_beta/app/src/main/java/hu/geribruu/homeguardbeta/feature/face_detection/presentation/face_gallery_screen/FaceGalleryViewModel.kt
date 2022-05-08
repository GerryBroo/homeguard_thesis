package hu.geribruu.homeguardbeta.feature.face_detection.presentation.face_gallery_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.feature.face_detection.data.repository.FaceRepositoryImpl
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import javax.inject.Inject

@HiltViewModel
class FaceGalleryViewModel @Inject constructor(
    repository: FaceRepositoryImpl
) : ViewModel() {

    val faces: LiveData<List<RecognizedFace>> = repository.getFaces().asLiveData()
}