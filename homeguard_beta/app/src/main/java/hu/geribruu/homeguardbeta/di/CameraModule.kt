package hu.geribruu.homeguardbeta.di

import android.content.Context
import androidx.camera.core.ImageCapture
import com.google.mlkit.vision.face.FaceDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.data.face.disk.FaceDiskDataSource
import hu.geribruu.homeguardbeta.domain.faceRecognition.CustomFaceDetector
import hu.geribruu.homeguardbeta.domain.faceRecognition.FaceManager
import hu.geribruu.homeguardbeta.domain.faceRecognition.ImageAnalyzer
import hu.geribruu.homeguardbeta.domain.faceRecognition.ImageManager
import hu.geribruu.homeguardbeta.domain.faceRecognition.PhotoCapture
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
    fun providesImageCapture(): ImageCapture {
        return ImageCapture.Builder().build()
    }

    @Singleton
    @Provides
    fun providesPhotoCapture(
        @ApplicationContext appContext: Context,
        imageCapture: ImageCapture,
    ): PhotoCapture {
        return PhotoCapture(appContext, imageCapture)
    }

    @Singleton
    @Provides
    fun providesCaptureManager(
        @ApplicationContext appContext: Context,
        photoCapture: PhotoCapture,
        repository: FaceDiskDataSource,
    ): FaceManager {
        return FaceManager(appContext, photoCapture, repository)
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
        faceManager: FaceManager,
    ): ImageAnalyzer {
        return ImageAnalyzer(appContext, faceDetector, faceManager)
    }

    @Provides
    @Singleton
    fun provideImageManager(imageAnalyzer: ImageAnalyzer): ImageManager {
        return ImageManager(imageAnalyzer)
    }
}
