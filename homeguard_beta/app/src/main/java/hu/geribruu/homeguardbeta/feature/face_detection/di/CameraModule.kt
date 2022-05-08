package hu.geribruu.homeguardbeta.feature.face_detection.di

import android.content.Context
import androidx.camera.core.ImageCapture
import com.google.mlkit.vision.face.FaceDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.feature.face_detection.data.repository.FaceRepositoryImpl
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.FaceCaptureManager
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.CustomFaceDetector
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.ImageAnalyzer
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.PhotoCapture
import hu.geribruu.homeguardbeta.feature.face_detection.domain.repository.FaceRepository
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
    fun providesImageCapture() : ImageCapture {
        return ImageCapture.Builder().build()
    }

    @Singleton
    @Provides
    fun providesPhotoCapture(@ApplicationContext appContext: Context, imageCapture : ImageCapture) : PhotoCapture {
        return PhotoCapture(appContext, imageCapture)
    }

    @Singleton
    @Provides
    fun providesCaptureManager(photoCapture: PhotoCapture, repository: FaceRepositoryImpl): FaceCaptureManager {
        return FaceCaptureManager(photoCapture, repository)
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
        faceCaptureManager: FaceCaptureManager
    ): ImageAnalyzer {
        return ImageAnalyzer(appContext, faceDetector, faceCaptureManager)
    }
}