package hu.geri.homeguard.domain.face

import hu.geri.homeguard.domain.history.HistoryUseCase

class FaceManager(
    private val historyUsaCase: HistoryUseCase
) {

    fun handleFaceDetection(name: String) {
        // TODO handle face history

        historyUsaCase.insertHistoryItem(name)
    }
}