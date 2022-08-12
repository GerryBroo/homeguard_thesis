package hu.geribruu.homeguardbeta.feature.face_detection.presentation.camera_preview_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.FaceCaptureManager
import hu.geribruu.homeguardbeta.feature.face_detection.domain.use_case.CameraUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraPreviewViewModel @Inject constructor(
    private var faceCaptureManager: FaceCaptureManager,
    private var cameraUseCases: CameraUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraPreviewUiState())
    val uiState: StateFlow<CameraPreviewUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

//    init {
//        getPreview()
//    }
//
//
//    private fun getPreview() {
//        fetchJob?.cancel()
//        fetchJob = cameraUseCases.getCameraPreviewUseCase()
//            .
//
//            viewModelScope.launch {
//            try {
//                val preview = captureFaceManager.previewBitmap
//
//                if (preview == null) {
//                    Log.d("ASD", "A viewmodelben is Null")
//                }
//
//                _uiState.update {
//                    it.copy(previewBitmap = preview)
//                }
//            } catch (ioe: IOException) {
//
//            }
//        }
//    }
}
