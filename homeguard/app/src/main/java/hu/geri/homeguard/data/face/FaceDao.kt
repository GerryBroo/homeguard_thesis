package hu.geri.homeguard.data.face

import androidx.room.*
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk

@Dao
interface FaceDao {

    @Query("SELECT * FROM faces")
    fun getFaces(): List<RecognizedFaceDisk>

    @Query("SELECT * FROM faces WHERE id = :id")
    fun getFaceById(id: Int): RecognizedFaceDisk?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFace(face: RecognizedFaceDisk)

    @Delete
    fun deleteFace(face: RecognizedFaceDisk)
}