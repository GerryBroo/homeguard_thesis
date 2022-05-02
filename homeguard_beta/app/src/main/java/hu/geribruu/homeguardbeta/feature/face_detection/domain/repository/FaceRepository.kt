package hu.geribruu.homeguardbeta.feature.face_detection.domain.repository

import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import kotlinx.coroutines.flow.Flow

interface FaceRepository {

    fun getFaces(): Flow<List<RecognizedFace>>

    suspend fun getFaceById(id: Int): RecognizedFace?

    suspend fun insertFace(face: RecognizedFace)

    suspend fun deleteFace(face: RecognizedFace)
}