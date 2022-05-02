package hu.geribruu.homeguardbeta.feature.face_detection.di

import android.content.Context
import com.google.mlkit.vision.face.FaceDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.CaptureManager
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.CustomFaceDetector
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.ImageAnalyzer
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
    fun providesCaptureManager(): CaptureManager {
        return CaptureManager()
    }

    @Singleton
    @Provides
    fun providesFaceDetector(): FaceDetector {
        return CustomFaceDetector().faceDetector
    }

    @Singleton
    @Provides
    fun providesImageAnalyzer(
        @ApplicationContext appContext: Context,
        faceDetector: FaceDetector,
        captureManager: CaptureManager
    ): ImageAnalyzer {
        return ImageAnalyzer(appContext, faceDetector, captureManager)
    }
}