package hu.geri.homeguard.injection

import androidx.camera.core.ImageCapture
import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import hu.geri.homeguard.domain.camera.PhotoCapture
import hu.geri.homeguard.domain.camera.usecase.CameraUseCases
import hu.geri.homeguard.domain.camera.usecase.GetPreviewUseCase
import hu.geri.homeguard.domain.camera.usecase.GetRecognizedFaceUseCase
import hu.geri.homeguard.domain.camera.usecase.GetRecognizedObjectUseCase
import hu.geri.homeguard.domain.face.FaceManager
import hu.geri.homeguard.domain.face.FaceUseCase
import hu.geri.homeguard.domain.face.FaceUseCaseImpl
import hu.geri.homeguard.domain.history.HistoryUseCase
import hu.geri.homeguard.domain.history.HistoryUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {

    // region Camera
    single { CameraUseCases(get(), get(), get()) }
    single { GetRecognizedObjectUseCase(get()) }
    single { GetRecognizedFaceUseCase(get()) }
    single { GetPreviewUseCase(get()) }

    fun provideImageCapture(): ImageCapture {
        return ImageCapture.Builder().build()
    }
    single { provideImageCapture() }
    single { PhotoCapture(androidContext(), get()) }
    // endregion

    // region Face
    single<FaceUseCase> { FaceUseCaseImpl(androidContext(), get(), get()) }
    single { FaceUseCaseImpl(androidContext(), get(), get()) }

    single { CustomAnalyzer(androidContext(), get(), get()) }

    single { FaceManager(get()) }
    // endregion

    // region History
    single<HistoryUseCase> { HistoryUseCaseImpl(get()) }
    single { HistoryUseCaseImpl(get()) }
    // endregion
}