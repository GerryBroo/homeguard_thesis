package hu.geribruu.homeguardbeta

import android.app.Application
import androidx.camera.core.ImageCapture
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import hu.geribruu.homeguardbeta.feature.face_detection.domain.face_recognition.FaceCaptureManager

@HiltAndroidApp
class HomaGuardApp : Application() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface InitializerEntryPoint {
        fun faceCaptureManager(): FaceCaptureManager
        fun imageCapture(): ImageCapture
    }
}
