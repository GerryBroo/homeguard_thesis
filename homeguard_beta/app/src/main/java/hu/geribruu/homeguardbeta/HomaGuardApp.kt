package hu.geribruu.homeguardbeta

import android.app.Application
import androidx.camera.core.ImageCapture
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.domain.faceRecognition.FaceManager

@HiltAndroidApp
class HomaGuardApp : Application() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface InitializerEntryPoint {
        fun faceCaptureManager(): FaceManager
        fun imageCapture(): ImageCapture
    }
}
