package hu.geribruu.homeguardbeta.domain.faceRecognition

import javax.inject.Inject

class ImageManager @Inject constructor(
    private val imageAnalyzer: ImageAnalyzer,
) {
    fun getPreviewName(): String {
        return imageAnalyzer.reco_name
    }
}
