package hu.geribruu.homeguardbeta.feature.face_detection.data.repository

import hu.geribruu.homeguardbeta.feature.face_detection.data.data_source.FaceDao
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import kotlinx.coroutines.flow.Flow

class FaceRepositoryImpl(
    private val dao: FaceDao
): FaceRepository {

    override fun getFaces(): Flow<List<RecognizedFace>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFaceById(id: Int): RecognizedFace? {
        TODO("Not yet implemented")
    }

    override suspend fun insertFace(face: RecognizedFace) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFace(face: RecognizedFace) {
        TODO("Not yet implemented")
    }


}