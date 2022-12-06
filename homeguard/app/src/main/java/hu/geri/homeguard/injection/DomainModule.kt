package hu.geri.homeguard.injection

import hu.geri.homeguard.domain.analyzer.CustomAnalyzer
import org.koin.dsl.module

val domainModule = module {
    single<CustomAnalyzer> { CustomAnalyzer() }
}