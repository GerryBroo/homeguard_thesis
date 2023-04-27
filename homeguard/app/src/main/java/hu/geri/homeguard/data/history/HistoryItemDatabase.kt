package hu.geri.homeguard.data.history

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.geri.homeguard.data.history.model.HistoryItemDisk

@Database(
    entities = [HistoryItemDisk::class], version = 1, exportSchema = false
)
abstract class HistoryItemDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryItemDao
}