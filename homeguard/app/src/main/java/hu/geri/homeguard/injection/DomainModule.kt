package hu.geri.homeguard.injection

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import hu.geri.homeguard.domain.camera.CameraUseCases
import hu.geri.homeguard.domain.camera.GetRecognizedObjectUseCase
import org.koin.dsl.module

val domainModule = module {
    single<CustomAnalyzer> { CustomAnalyzer() }

    // region Camera UseCase
    single<CameraUseCases> { CameraUseCases(get(), get()) }
    single<GetRecognizedObjectUseCase> { GetRecognizedObjectUseCase(get()) }
    single<GetRecognizedObjectUseCase> { GetRecognizedObjectUseCase(get()) }
    // endregion
}