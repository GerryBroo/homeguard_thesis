package hu.geribruu.homeguardbeta.data.face.disk

import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FaceDiskDataSource @Inject constructor(
    private val dao: FaceDao,
) {

    fun getFaces(): Flow<List<RecognizedFace>> {
        return dao.getFace()
    }

    suspend fun getFaceById(id: Int): RecognizedFace? {
        return dao.getFaceById(id)
    }

    suspend fun insertFace(face: RecognizedFace) {
        dao.insertFace(face)
    }

    suspend fun deleteFace(face: RecognizedFace) {
        dao.deleteFace(face)
    }
}
