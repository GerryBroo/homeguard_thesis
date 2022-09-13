package hu.geribruu.homeguardbeta.domain.faceRecognition

import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class FaceCaptureManager @Inject constructor(
    private val photoCapture: PhotoCapture,
    private val repository: FaceDiskDataSource,
) {

    fun manageNewFace(name: String) {
        val date = SimpleDateFormat(
            PhotoCapture.FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())
        val url = photoCapture.takePhoto()

        GlobalScope.launch {
            repository.insertFace(RecognizedFace(0, name, date, url))
        }
    }
}
