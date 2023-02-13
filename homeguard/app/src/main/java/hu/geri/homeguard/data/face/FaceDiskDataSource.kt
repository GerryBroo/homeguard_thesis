package hu.geri.homeguard.data.face

import hu.geri.homeguard.data.face.model.RecognizedFaceDisk
import javax.inject.Inject

class FaceDiskDataSource @Inject constructor(
    private val dao: FaceDao,
) {

    fun getFaces(): List<RecognizedFaceDisk> {
        return dao.getFaces()
    }

    suspend fun getFaceById(id: Int): RecognizedFaceDisk? {
        return dao.getFaceById(id)
    }

    suspend fun insertFace(face: RecognizedFaceDisk) {
        dao.insertFace(face)
    }

    suspend fun deleteFace(face: RecognizedFaceDisk) {
        dao.deleteFace(face)
    }
}