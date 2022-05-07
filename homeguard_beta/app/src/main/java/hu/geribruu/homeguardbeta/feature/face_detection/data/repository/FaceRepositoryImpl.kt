package hu.geribruu.homeguardbeta.feature.face_detection.data.repository

import hu.geribruu.homeguardbeta.feature.face_detection.data.data_source.FaceDao
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import kotlinx.coroutines.flow.Flow

class FaceRepositoryImpl(
    private val dao: FaceDao
): FaceRepository {

    override fun getFaces(): Flow<List<RecognizedFace>> {
        dao.getFace()
    }

    override suspend fun getFaceById(id: Int): RecognizedFace? {
        dao.getFaceById(id)
    }

    override suspend fun insertFace(face: RecognizedFace) {
        dao.insertFace(face)
    }

    override suspend fun deleteFace(face: RecognizedFace) {
        dao.deleteFace()
    }


}