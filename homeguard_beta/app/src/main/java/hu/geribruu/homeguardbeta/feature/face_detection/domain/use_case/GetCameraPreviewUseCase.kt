package hu.geribruu.homeguardbeta.feature.face_detection.domain.use_case

import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.ImageManager

class GetCameraPreviewUseCase(
    private val imageManager: ImageManager
) {

    operator fun invoke(): String {
        return imageManager.getPreviewName()
    }
}