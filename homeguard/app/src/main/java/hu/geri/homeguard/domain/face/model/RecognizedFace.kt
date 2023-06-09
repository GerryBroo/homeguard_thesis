package hu.geri.homeguard.domain.face.model


import android.graphics.Bitmap
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk

data class RecognizedFace(
    var id: Long,
    var name: String,
    var captureDate: String,
    var bitmap: Bitmap
)

fun List<RecognizedFaceDisk>.toRecognizedFaces() = map { recognizedFace ->
    recognizedFace.toRecognizedFace()
}

fun RecognizedFaceDisk.toRecognizedFace() =
    RecognizedFace(
        id = id,
        name = name,
        captureDate = captureDate,
        bitmap = bitmap
    )
