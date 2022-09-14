package hu.geribruu.homeguardbeta.data.face.disk

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceDao {

    @Query("SELECT * FROM face")
    fun getFace(): Flow<List<RecognizedFace>>

    @Query("SELECT * FROM face WHERE id = :id")
    fun getFaceById(id: Int): RecognizedFace?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFace(face: RecognizedFace)

    @Delete
    suspend fun deleteFace(face: RecognizedFace)

    @Query("DELETE FROM face")
    fun deleteAll()
}
