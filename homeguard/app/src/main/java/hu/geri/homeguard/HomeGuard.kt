package hu.geri.homeguard

import android.app.Application
import hu.geri.homeguard.injection.dataModule
import hu.geri.homeguard.injection.domainModule
import hu.geri.homeguard.injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HomeGuard : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HomeGuard)
            modules(
                listOf(
                    dataModule,
                    domainModule,
                    viewModelModule
                )
            )
        }
    }
}