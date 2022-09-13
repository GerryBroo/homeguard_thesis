package hu.geribruu.homeguardbeta.domain.faceRecognition.useCase

import hu.geribruu.homeguardbeta.domain.faceRecognition.ImageManager

class GetCameraPreviewUseCase(
    private val imageManager: ImageManager
) {

    operator fun invoke(): String {
        return imageManager.getPreviewName()
    }
}