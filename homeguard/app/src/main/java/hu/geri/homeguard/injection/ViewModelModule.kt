package hu.geri.homeguard.injection

import hu.geri.homeguard.ui.camera.CameraViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CameraViewModel(get()) }
}