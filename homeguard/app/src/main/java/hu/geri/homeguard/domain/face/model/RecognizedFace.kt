package hu.geri.homeguard.domain.face.model


import hu.geri.homeguard.data.face.model.RecognizedFaceDisk

data class RecognizedFace(
    var id: Long,
    var name: String,
    var captureDate: String,
    var faceUrl: String,
)

fun List<RecognizedFaceDisk>.toRecognizedFaces() = map { recognizedFace ->
    recognizedFace.toRecognizedFace()
}

fun RecognizedFaceDisk.toRecognizedFace() =
    RecognizedFace(
        id = id,
        name = name,
        captureDate = captureDate,
        faceUrl = faceUrl
    )
