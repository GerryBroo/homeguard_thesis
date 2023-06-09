package hu.geri.homeguard.data.face

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk
import hu.geri.homeguard.data.history.util.BitmapConverter
import hu.geri.homeguard.data.history.util.EnumConverter
import hu.geri.homeguard.data.history.util.FloatArrayConverter

@Database(
    entities = [RecognizedFaceDisk::class], version = 3, exportSchema = false
)
@TypeConverters(BitmapConverter::class)
abstract class FaceDatabase : RoomDatabase() {
    abstract fun faceDao(): FaceDao
}