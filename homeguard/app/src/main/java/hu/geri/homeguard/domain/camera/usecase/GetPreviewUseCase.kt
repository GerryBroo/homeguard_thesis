package hu.geri.homeguard.domain.camera.usecase

import hu.geri.homeguard.domain.analyzer.Azigen
import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow

class GetPreviewUseCase(
    private val analyzer: CustomAnalyzer
) {
    operator fun invoke(): MutableStateFlow<Azigen> {
        return analyzer.prevViewFace
    }
}