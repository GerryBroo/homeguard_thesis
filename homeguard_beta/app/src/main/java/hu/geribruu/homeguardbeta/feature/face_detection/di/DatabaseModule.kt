package hu.geribruu.homeguardbeta.feature.face_detection.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.feature.face_detection.data.data_source.FaceDatabase
import hu.geribruu.homeguardbeta.feature.face_detection.data.repository.FaceRepositoryImpl
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFaceDatabase(app: Application): FaceDatabase {
        return Room.databaseBuilder(
            app,
            FaceDatabase::class.java,
            FaceDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFaceRepository(db: FaceDatabase): FaceRepository {
        return FaceRepositoryImpl(db.faceDao)
    }
}