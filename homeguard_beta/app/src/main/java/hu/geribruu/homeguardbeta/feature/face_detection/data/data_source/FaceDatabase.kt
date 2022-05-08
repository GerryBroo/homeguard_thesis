package hu.geribruu.homeguardbeta.feature.face_detection.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import hu.geribruu.homeguardbeta.feature.face_detection.domain.model.RecognizedFace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [RecognizedFace::class],
    version = 1
)
abstract class FaceDatabase : RoomDatabase() {

    abstract fun faceDao(): FaceDao

    private class FaceDatabaseCallback (
        private val scope : CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch {
                    val faceDao = database.faceDao()

                    faceDao.deleteAll()
                }
            }
        }
    }

    companion object{
        @Volatile
        private var INSTANCE : FaceDatabase? = null

        fun getDatabase(
            context : Context,
            scope : CoroutineScope
        ) : FaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FaceDatabase::class.java,
                    "face"
                )
                    .addCallback(FaceDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}