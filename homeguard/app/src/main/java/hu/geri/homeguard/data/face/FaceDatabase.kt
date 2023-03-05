package hu.geri.homeguard.data.face

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk

@Database(
    entities = [RecognizedFaceDisk::class],
    version = 1,
    exportSchema = false
)
abstract class FaceDatabase : RoomDatabase() {
    abstract fun faceDao(): FaceDao
}