package hu.geri.homeguard.injection

import androidx.room.Room
import hu.geri.homeguard.data.face.FaceDatabase
import hu.geri.homeguard.data.face.FaceDiskDataSource
import hu.geri.homeguard.data.history.HistoryItemDatabase
import hu.geri.homeguard.data.history.HistoryItemDiskDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {

    // region Face
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

    // region History
    single {
        Room.databaseBuilder(
            androidApplication(),
            HistoryItemDatabase::class.java,
            "histories"
        ).build()
    }
    single { get<HistoryItemDatabase>().historyDao() }
    single { HistoryItemDiskDataSource(get()) }
    // endregion
}