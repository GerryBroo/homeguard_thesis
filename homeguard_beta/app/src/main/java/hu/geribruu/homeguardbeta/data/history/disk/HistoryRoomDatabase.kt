package hu.geribruu.homeguardbeta.data.history.disk

import androidx.room.Database
import androidx.room.RoomDatabase
import hu.geribruu.homeguardbeta.data.history.disk.model.RoomHistoryItem

@Database(
    entities = [RoomHistoryItem::class],
    version = 1,
    exportSchema = false
)
abstract class HistoryRoomDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}