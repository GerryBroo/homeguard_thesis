package hu.geri.homeguard.injection

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import hu.geri.homeguard.domain.camera.CameraUseCases
import hu.geri.homeguard.domain.camera.GetRecognizedObjectUseCase
import org.koin.dsl.module

val domainModule = module {
    single<CustomAnalyzer> { CustomAnalyzer() }

    single<GetRecognizedObjectUseCase> { GetRecognizedObjectUseCase(get()) }
    single<CameraUseCases> { CameraUseCases(get()) }
}