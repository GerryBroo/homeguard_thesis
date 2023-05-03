package hu.geri.homeguard.data.history.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "histories")
data class HistoryItemDisk (

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "captureDate")
    var captureDate: String,
)