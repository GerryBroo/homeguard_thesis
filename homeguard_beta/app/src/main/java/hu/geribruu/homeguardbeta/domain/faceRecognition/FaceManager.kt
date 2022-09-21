package hu.geribruu.homeguardbeta.domain.faceRecognition

import android.content.Context
import android.util.Log
import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.data.history.HistoryRepository
import hu.geribruu.homeguardbeta.data.history.disk.model.RoomHistoryItem
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.SimilarityClassifier
import hu.geribruu.homeguardbeta.domain.faceRecognition.util.insertToSP
import hu.geribruu.homeguardbeta.domain.history.model.FaceCapturedItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class FaceManager @Inject constructor(
    private var context: Context,
    private val photoCapture: PhotoCapture,
    private val faceDiskDataSource: FaceDiskDataSource,
    private val repositoryHistory: HistoryRepository,
) {

    private var detectedFace: MutableList<String> = mutableListOf()
    private var faceCapturedItems: MutableList<FaceCapturedItem> = mutableListOf()

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

    private fun getFaceCapturedIfInList(name: String): FaceCapturedItem? {
        faceCapturedItems.forEach { face ->
            if (face.name == name) {
                return face
            }
        }
        return null
    }

    fun manageFace(name: String) {
        if (name !in detectedFace &&
            name != "Unknown" &&
            name != "No Face Detected!" &&
            name != ""
        ) {
            val faceCaptured = getFaceCapturedIfInList(name)
            if (faceCaptured == null) {
                detectedFace.add(name)
                faceCapturedItems.add(FaceCapturedItem(name))
                getFaceCapturedIfInList(name)?.startTimer()
            }

            if (faceCaptured != null && faceCaptured.isDetectable) {
                detectedFace.add(name)
                faceCaptured.startTimer()
            }
        }

        if (name in detectedFace &&
            name != "Unknown" &&
            name != "No Face Detected!" &&
            name != ""
        ) {
            faceCapturedItems.forEach { face ->
                if (face.name == name) {
                    if (!face.isCounterRunning && face.isDetectable) {
                        face.startTimer()
                    }
                    face.runTimer()
                }
            }
        } else {
            faceCapturedItems.forEach { face ->
                face.stopTimer()
            }
        }

        faceCapturedItems.forEach { face ->
            if (face.isDetected) {
                detectedFace.remove(face.name)
                saveFaceHistory(face.name)
                face.reset()
                face.startAvailableTimer()
            }
        }
    }

    private fun saveFaceHistory(name: String) {
        GlobalScope.launch {
            val date = SimpleDateFormat(
                PhotoCapture.FILENAME_FORMAT,
                Locale.US
            ).format(System.currentTimeMillis())

            repositoryHistory.insertHistory(
                RoomHistoryItem(0, name, date)
            )
        }
    }
}
