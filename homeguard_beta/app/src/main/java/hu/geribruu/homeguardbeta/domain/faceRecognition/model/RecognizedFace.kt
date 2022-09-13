package hu.geribruu.homeguardbeta.domain.faceRecognition.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "face")
data class RecognizedFace(

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
