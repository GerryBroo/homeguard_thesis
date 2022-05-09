package hu.geribruu.homeguardbeta.feature.face_detection.di

import android.content.Context
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
    fun provideFaceDatabase(@ApplicationContext appContext: Context): FaceDatabase {
        return FaceDatabase.getDatabase(appContext, applicationScope)
    }

    @Provides
    fun provideFaceDao(database: FaceDatabase): FaceDao = database.faceDao()

    @Singleton
    @Provides
    fun provideRepository(faceDao: FaceDao): FaceRepositoryImpl {
        return FaceRepositoryImpl(faceDao)
    }
}