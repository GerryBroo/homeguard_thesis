package hu.geribruu.homeguardbeta.feature.face_detection.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.feature.face_detection.data.data_source.FaceDao
import hu.geribruu.homeguardbeta.feature.face_detection.data.data_source.FaceDatabase
import hu.geribruu.homeguardbeta.feature.face_detection.data.repository.FaceRepositoryImpl
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val applicationScope = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): FaceDatabase {
        return FaceDatabase.getDatabase(appContext, applicationScope)
    }

    @Provides
    fun provideFaceDao(database: FaceDatabase): FaceDao = database.faceDao()

    @Provides
    @Singleton
    fun provideFaceRepository(db: FaceDatabase): FaceRepositoryImpl {
        return FaceRepositoryImpl(db.faceDao())
    }
}