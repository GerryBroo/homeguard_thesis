package hu.geribruu.homeguardbeta.feature.face_detection.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecognizedFace(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id : Long,

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "capture_date")
    var captureDate : String,

    @ColumnInfo(name = "face_url")
    var faceUrl : String,
)
