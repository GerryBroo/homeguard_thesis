package hu.geribruu.homeguardbeta.feature.di

import android.content.Context
import androidx.camera.core.ImageAnalysis
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.feature.face_recognition.ImageAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CameraModule {

    @Singleton
    @Provides
    fun providesCameraExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }

    @Singleton
    @Provides
    fun providesImageAnalyzer(@ApplicationContext appContext: Context) : ImageAnalyzer {
        return ImageAnalyzer(appContext)
    }
}