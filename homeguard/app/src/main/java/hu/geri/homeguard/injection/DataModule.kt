package hu.geri.homeguard.injection

import android.app.Application
import androidx.room.Room
import hu.geri.homeguard.data.face.FaceDao
import hu.geri.homeguard.data.face.FaceDatabase
import hu.geri.homeguard.data.face.FaceDiskDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {

    // region Face Database
//    fun provideFaceDatabase(application: Application): FaceDatabase {
//        return Room.databaseBuilder(application, FaceDatabase::class.java, "faces")
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    fun provideFaceDao(database: FaceDatabase): FaceDao {
//        return database.faceDao()
//    }
//
//    single { provideFaceDatabase(androidApplication()) }
//    single { provideFaceDao(get()) }
//    single { FaceDiskDataSource(get()) }

    single {
        Room.databaseBuilder(
            androidApplication(),
            FaceDatabase::class.java,
            "faces"
        ).build()
    }
    single { get<FaceDatabase>().faceDao() }
    single { FaceDiskDataSource(get()) }
    // endregion
}