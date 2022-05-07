package hu.geribruu.homeguardbeta.feature.face_detection.data.data_source

import androidx.room.*
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceDao {

    @Query("SELECT * FROM face")
    fun getFace(): Flow<List<RecognizedFace>>

    @Query("SELECT * FROM face WHERE id = :id")
    suspend fun getFaceById(id: Int): RecognizedFace?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFace(face: RecognizedFace)

    @Delete
    suspend fun deleteFace(face: RecognizedFace)
}