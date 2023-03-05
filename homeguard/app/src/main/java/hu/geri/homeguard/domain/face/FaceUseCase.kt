package hu.geri.homeguard.domain.face

import hu.geri.homeguard.data.face.FaceDiskDataSource
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk
import hu.geri.homeguard.domain.face.model.RecognizedFace
import hu.geri.homeguard.domain.face.model.toRecognizedFaces
import java.text.SimpleDateFormat
import java.util.*

interface FaceUseCase {
    fun getFaces(): FacesResult
    suspend fun saveFace(name: String)
}

class FaceUseCaseImpl(
    private val faceDiskDataSource: FaceDiskDataSource
) : FaceUseCase {

    override fun getFaces(): FacesResult {
        val result = faceDiskDataSource.getFaces()
        if (result.isEmpty()) {
            return FacesEmptyError
        }
        return FacesSuccess(result.toRecognizedFaces())
    }

    override suspend fun saveFace(name: String) {
        val date = SimpleDateFormat(
            DATE_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        faceDiskDataSource.insertFace(RecognizedFaceDisk(0, name, date, ""))
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

sealed interface FacesResult
object FacesEmptyError : FacesResult
data class FacesSuccess(val face: List<RecognizedFace>) : FacesResult