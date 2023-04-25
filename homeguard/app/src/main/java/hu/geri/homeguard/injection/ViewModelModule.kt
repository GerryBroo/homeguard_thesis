package hu.geri.homeguard.injection

import hu.geri.homeguard.ui.addface.AddFaceViewModel
import hu.geri.homeguard.ui.camera.CameraViewModel
import hu.geri.homeguard.ui.facegallery.FaceGalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CameraViewModel(get()) }
    viewModel { FaceGalleryViewModel(get()) }
    viewModel { AddFaceViewModel(get(), get()) }
}