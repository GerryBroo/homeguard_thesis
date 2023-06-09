package hu.geri.homeguard.data.history

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.geri.homeguard.data.history.model.HistoryItemDisk
import hu.geri.homeguard.data.history.util.BitmapConverter
import hu.geri.homeguard.data.history.util.EnumConverter
import hu.geri.homeguard.data.history.util.FloatArrayConverter

@Database(
    entities = [HistoryItemDisk::class], version = 2, exportSchema = false
)
@TypeConverters(BitmapConverter::class, EnumConverter::class, FloatArrayConverter::class)
abstract class HistoryItemDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryItemDao
}