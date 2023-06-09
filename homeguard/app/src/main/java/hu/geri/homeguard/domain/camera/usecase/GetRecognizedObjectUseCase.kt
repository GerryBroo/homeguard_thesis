package hu.geri.homeguard.domain.camera.usecase

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow

class GetRecognizedObjectUseCase(
    private val analyzer: CustomAnalyzer
) {
    operator fun invoke(): MutableStateFlow<String> {
        return analyzer.recognizedObject
    }
}