package hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition

import javax.inject.Inject

class ImageManager @Inject constructor(
    private val imageAnalyzer: ImageAnalyzer
) {
    fun getPreviewName(): String {
        return imageAnalyzer.reco_name
    }
}