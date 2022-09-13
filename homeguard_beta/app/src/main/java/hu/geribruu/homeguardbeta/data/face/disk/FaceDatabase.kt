package hu.geribruu.homeguardbeta.data.face.disk

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.geribruu.homeguardbeta.domain.faceRecognition.model.RecognizedFace

@Database(
    entities = [RecognizedFace::class],
    version = 1,
    exportSchema = false
)
abstract class FaceDatabase : RoomDatabase() {

    abstract fun faceDao(): FaceDao
}
