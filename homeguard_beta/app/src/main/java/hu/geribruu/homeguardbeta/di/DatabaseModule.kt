package hu.geribruu.homeguardbeta.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.data.face.disk.FaceDao
import hu.geribruu.homeguardbeta.data.face.disk.FaceDatabase
import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.data.history.HistoryRepository
import hu.geribruu.homeguardbeta.data.history.disk.HistoryDao
import hu.geribruu.homeguardbeta.data.history.disk.HistoryRoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideFaceDatabase(@ApplicationContext appContext: Context): FaceDatabase {
        return Room.databaseBuilder(
            appContext,
            FaceDatabase::class.java,
            "face"
        ).build()
    }

    @Provides
    fun provideFaceDao(database: FaceDatabase): FaceDao = database.faceDao()

    @Singleton
    @Provides
    fun provideFaceDiskDataSource(faceDao: FaceDao): FaceDiskDataSource {
        return FaceDiskDataSource(faceDao)
    }

    @Singleton
    @Provides
    fun provideHistoryRoomDatabase(@ApplicationContext appContext: Context): HistoryRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            HistoryRoomDatabase::class.java,
            "hitories"
        ).build()
    }

    @Provides
    fun provideHistoryDao(database: HistoryRoomDatabase): HistoryDao = database.historyDao()

    @Singleton
    @Provides
    fun provideHistoryRepository(historyDao: HistoryDao): HistoryRepository {
        return HistoryRepository(historyDao)
    }
}
