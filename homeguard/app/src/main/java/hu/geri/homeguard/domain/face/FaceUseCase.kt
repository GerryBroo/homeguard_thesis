package hu.geri.homeguard.domain.face

import hu.geri.homeguard.data.face.FaceDiskDataSource
import hu.geri.homeguard.domain.face.model.RecognizedFace
import hu.geri.homeguard.domain.face.model.toRecognizedFaces

interface FaceUseCase {
    suspend fun getFaces(): FacesResult
}

class FaceUseCaseImpl(
    private val faceDiskDataSource: FaceDiskDataSource
) : FaceUseCase {

    override suspend fun getFaces(): FacesResult {
        val result = faceDiskDataSource.getFaces()
        if (result.isEmpty()) {
            return FacesEmptyError
        }
        return FacesSuccess(result.toRecognizedFaces())
    }
}

sealed interface FacesResult
object FacesEmptyError : FacesResult
data class FacesSuccess(val face: List<RecognizedFace>) : FacesResult