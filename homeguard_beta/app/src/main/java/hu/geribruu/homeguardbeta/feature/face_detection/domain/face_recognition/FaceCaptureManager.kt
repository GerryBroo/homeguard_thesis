package hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition

import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FaceCaptureManager @Inject constructor(
    private val photoCapture: PhotoCapture,
    private val repository: FaceRepository
) {
    fun manageNewFace(name: String) {
        val date = SimpleDateFormat(PhotoCapture.FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val url = photoCapture.takePhoto()

        GlobalScope.launch {
            repository.insertFace(RecognizedFace(0, name, date, url))
        }
    }
}