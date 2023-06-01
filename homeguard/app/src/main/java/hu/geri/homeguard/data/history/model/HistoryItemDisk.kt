package hu.geri.homeguard.data.history.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import hu.geri.homeguard.data.history.util.EnumConverter

@Entity(tableName = "histories")
data class HistoryItemDisk(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "captureDate")
    var captureDate: String,

    @ColumnInfo(name = "bitmap")
    val bitmap: Bitmap,

    @ColumnInfo(name = "embeedings")
    val embeedings: Array<FloatArray>,

    @ColumnInfo(name = "historyType")
    val yourEnum: HistoryEnum
)

@TypeConverters(EnumConverter::class)
enum class HistoryEnum {
    FACE,
    UNKNOWN_FACE,
    TRUCK,
    POSTMAN
}