package hu.geribruu.homeguardbeta.feature.face_detection.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace

@Database(
    entities = [RecognizedFace::class],
    version = 1
)
abstract class FaceDatabase : RoomDatabase() {

    abstract val faceDao: FaceDao

    companion object {
        const val DATABASE_NAME = "faces_db"
    }
}