package hu.geri.homeguard.domain.camera

data class CameraUseCases(
    val getRecognizedObjectUseCase: GetRecognizedObjectUseCase,
    val getRecognizedFaceUseCase: GetRecognizedFaceUseCase
)