package hu.geri.homeguard.domain.face

import android.content.Context
import hu.geri.homeguard.domain.analyzer.model.SimilarityClassifier
import hu.geri.homeguard.domain.face.util.insertToSP
import hu.geri.homeguard.domain.face.util.readFromSP
import hu.geri.homeguard.domain.history.HistoryUseCase

class FaceManager(
    private val context: Context,
    private val historyUsaCase: HistoryUseCase
) {
    var registered: HashMap<String?, SimilarityClassifier.Recognition> =
        HashMap<String?, SimilarityClassifier.Recognition>()

    init {
        registered = readFromSP(context)
    }

    fun handleFaceDetection(name: String) {
        // TODO handle face history

        historyUsaCase.insertHistoryItem(name)
    }

    fun setNewFace(name: String, emb: Array<FloatArray>) {
        val result = SimilarityClassifier.Recognition("0", "", -1f)
        result.extra = emb
        registered[name] = result

        insertToSP(context, registered)
    }
}