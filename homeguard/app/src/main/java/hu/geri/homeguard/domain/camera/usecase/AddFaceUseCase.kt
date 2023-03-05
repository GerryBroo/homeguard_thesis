package hu.geri.homeguard.domain.camera.usecase

import hu.geri.homeguard.domain.analyzer.Azigen
import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow

class AddFaceUseCase(
    private val analyzer: CustomAnalyzer,
) {
    operator fun invoke(name: String, emb: Array<FloatArray>) {
        return analyzer.setNewFace(name, emb)
    }
}