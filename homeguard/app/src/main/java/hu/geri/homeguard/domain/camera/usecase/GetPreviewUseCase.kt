package hu.geri.homeguard.domain.camera.usecase

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import hu.geri.homeguard.domain.analyzer.model.AddFaceData
import hu.geri.homeguard.domain.camera.PhotoCapture

class GetPreviewUseCase(
    private val analyzer: CustomAnalyzer
) {
    operator fun invoke(): AddFaceData {
        return analyzer.newFaceEvent()
    }
}