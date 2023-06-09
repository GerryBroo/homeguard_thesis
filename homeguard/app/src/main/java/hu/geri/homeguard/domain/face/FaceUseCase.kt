package hu.geri.homeguard.domain.face

import android.content.Context
import android.graphics.Bitmap
import hu.geri.homeguard.data.face.FaceDiskDataSource
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk
import hu.geri.homeguard.data.face.model.toRecognizedFaceDisk
import hu.geri.homeguard.domain.face.model.RecognizedFace
import hu.geri.homeguard.domain.face.model.toRecognizedFaces
import hu.geri.homeguard.domain.face.util.deleteFromSP
import java.text.SimpleDateFormat
import java.util.*

interface FaceUseCase {
    fun getFaces(): FacesResult
    suspend fun saveFace(name: String, bitmap: Bitmap, emb: Array<FloatArray>)
    suspend fun deleteFace(face: RecognizedFace)
}

class FaceUseCaseImpl(
    private val context: Context,
    private val faceDiskDataSource: FaceDiskDataSource,
    private val faceManager: FaceManager
) : FaceUseCase {

    override fun getFaces(): FacesResult {
        val result = faceDiskDataSource.getFaces()
        if (result.isEmpty()) {
            return FacesEmptyError
        }
        return FacesSuccess(result.toRecognizedFaces())
    }

    override suspend fun saveFace(name: String, bitmap: Bitmap, emb: Array<FloatArray>) {
        val date = SimpleDateFormat(
            DATE_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        faceDiskDataSource.insertFace(RecognizedFaceDisk(0, name, date, bitmap))
        faceManager.setNewFace(name, emb)
    }

    override suspend fun deleteFace(face: RecognizedFace) {
        faceDiskDataSource.deleteFace(face.toRecognizedFaceDisk())
        deleteFromSP(context, face.name)
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

sealed interface FacesResult
object FacesEmptyError : FacesResult
data class FacesSuccess(val face: List<RecognizedFace>) : FacesResult