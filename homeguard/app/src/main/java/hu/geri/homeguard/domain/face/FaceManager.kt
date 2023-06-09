package hu.geri.homeguard.domain.face

import android.content.Context
import android.graphics.Bitmap
import hu.geri.homeguard.data.history.model.HistoryEnum
import hu.geri.homeguard.domain.analyzer.model.SimilarityClassifier
import hu.geri.homeguard.domain.face.util.insertToSP
import hu.geri.homeguard.domain.face.util.readFromSP
import hu.geri.homeguard.domain.history.HistoryUseCase
import hu.geri.homeguard.domain.history.model.CapturedFace

class FaceManager(
    private val context: Context,
    private val historyUsaCase: HistoryUseCase
) {
    var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>()

    private val capturedFaces: MutableList<CapturedFace> = mutableListOf()

    init {
        registered = readFromSP(context)
    }

    private fun isFaceInCapturedFace(name: String): Boolean {
        capturedFaces.forEach { face ->
            if (face.name == name) {
                return true
            }
        }
        return false
    }

    fun handleFaceDetection(name: String, addFaceBitmap: Bitmap, embeedings: Array<FloatArray>) {
        if (!isFaceInCapturedFace(name)) {
            val face = CapturedFace(name)
            face.startOnScreenTimer()
            capturedFaces.add(face)
        }

        capturedFaces.forEach { face ->
            if (face.isDetectable) {
                face.isOnScreen = face.name == name
            }

            if (face.isDetected) {
                val historyType =
                    if (face.name == "Unknown") HistoryEnum.UNKNOWN_FACE else HistoryEnum.FACE
                historyUsaCase.insertHistoryItem(face.name, addFaceBitmap, embeedings, historyType)
                face.startCaptureTimer()
                face.isDetected = false
            }

            if (face.isDeletable) {
                capturedFaces.remove(face)
            }
        }
    }

    fun setNewFace(name: String, emb: Array<FloatArray>) {
        val result = SimilarityClassifier.Recognition("0", "", -1f)
        result.extra = emb
        registered[name] = result

        insertToSP(context, registered)
    }
}