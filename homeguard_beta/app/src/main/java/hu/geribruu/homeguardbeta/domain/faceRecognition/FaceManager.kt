package hu.geribruu.homeguardbeta.domain.faceRecognition

import android.content.Context
import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.SimilarityClassifier
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.insertToSP
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class FaceManager @Inject constructor(
    private var context: Context,
    private val photoCapture: PhotoCapture,
    private val faceDiskDataSource: FaceDiskDataSource,
) {

    fun manageNewFace(
        registered: HashMap<String?, SimilarityClassifier.Recognition>,
        name: String,
    ) {
        insertToSP(context, registered)

        val date = SimpleDateFormat(
            PhotoCapture.FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())
        val url = photoCapture.takePhoto()

        GlobalScope.launch {
            faceDiskDataSource.insertFace(RecognizedFace(0, name, date, url))
        }
    }

    fun manageFace(name: String) {

    }
}
