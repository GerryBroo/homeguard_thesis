package hu.geri.homeguard.data.face.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.geri.homeguard.domain.face.model.RecognizedFace

@Entity(tableName = "faces")
data class RecognizedFaceDisk(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "capture_date")
    var captureDate: String,

    @ColumnInfo(name = "face_url")
    var faceUrl: String,
)

fun RecognizedFace.toRecognizedFaceDisk() =
    RecognizedFaceDisk(
        id = id,
        name = name,
        captureDate = captureDate,
        faceUrl = faceUrl
    )
