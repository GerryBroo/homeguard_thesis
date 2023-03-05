package hu.geri.homeguard.domain.camera.usecase

data class CameraUseCases(
    val getRecognizedObjectUseCase: GetRecognizedObjectUseCase,
    val getRecognizedFaceUseCase: GetRecognizedFaceUseCase,
    val getPreviewUseCase: GetPreviewUseCase,
    val addFaceUseCase: AddFaceUseCase
)