package hu.geri.homeguard.injection

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import hu.geri.homeguard.domain.camera.usecase.CameraUseCases
import hu.geri.homeguard.domain.camera.usecase.GetPreviewUseCase
import hu.geri.homeguard.domain.camera.usecase.GetRecognizedFaceUseCase
import hu.geri.homeguard.domain.camera.usecase.GetRecognizedObjectUseCase
import hu.geri.homeguard.domain.face.FaceUseCase
import hu.geri.homeguard.domain.face.FaceUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    single { CustomAnalyzer() }

    // region Camera UseCase
    single { CameraUseCases(get(), get(), get()) }
    single { GetRecognizedObjectUseCase(get()) }
    single { GetRecognizedFaceUseCase(get()) }
    single { GetPreviewUseCase(get()) }
    // endregion

    // region Face recognition
    single<FaceUseCase> { FaceUseCaseImpl(get()) }
    single { FaceUseCaseImpl(get()) }
    // endregion
}