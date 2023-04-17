package hu.geri.homeguard.domain.face

import hu.geri.homeguard.data.face.FaceDiskDataSource
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk
import hu.geri.homeguard.data.face.model.toRecognizedFaceDisk
import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import hu.geri.homeguard.domain.camera.PhotoCapture
import hu.geri.homeguard.domain.face.model.RecognizedFace
import hu.geri.homeguard.domain.face.model.toRecognizedFaces
import hu.geri.homeguard.domain.face.util.deleteFaceImage
import java.text.SimpleDateFormat
import java.util.*

interface FaceUseCase {
    fun getFaces(): FacesResult
    suspend fun saveFace(name: String, emb: Array<FloatArray>, url: String)
    suspend fun deleteFace(face: RecognizedFace)
}

class FaceUseCaseImpl(
    private val faceDiskDataSource: FaceDiskDataSource,
    private val analyzer: CustomAnalyzer
) : FaceUseCase {

    override fun getFaces(): FacesResult {
        val result = faceDiskDataSource.getFaces()
        if (result.isEmpty()) {
            return FacesEmptyError
        }
        return FacesSuccess(result.toRecognizedFaces())
    }

    override suspend fun saveFace(name: String, emb: Array<FloatArray>, url: String) {
        val date = SimpleDateFormat(
            DATE_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        faceDiskDataSource.insertFace(RecognizedFaceDisk(0, name, date, url))
        analyzer.setNewFace(name, emb)
    }

    override suspend fun deleteFace(face: RecognizedFace) {
        faceDiskDataSource.deleteFace(face.toRecognizedFaceDisk())
        deleteFaceImage(face.faceUrl)
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

sealed interface FacesResult
object FacesEmptyError : FacesResult
data class FacesSuccess(val face: List<RecognizedFace>) : FacesResult