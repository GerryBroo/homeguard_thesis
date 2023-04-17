package hu.geri.homeguard.domain.face.util

import java.io.File

fun deleteFaceImage(url: String) {
    val file = File(url)
    file.delete()
}