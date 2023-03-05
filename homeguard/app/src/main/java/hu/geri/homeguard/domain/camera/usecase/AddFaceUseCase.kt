package hu.geri.homeguard.domain.camera.usecase

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer

class AddFaceUseCase(
    private val analyzer: CustomAnalyzer,
) {
    suspend operator fun invoke(name: String, emb: Array<FloatArray>) {
        return analyzer.setNewFace(name, emb)
    }
}