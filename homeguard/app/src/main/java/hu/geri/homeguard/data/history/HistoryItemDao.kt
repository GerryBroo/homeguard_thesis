package hu.geri.homeguard.data.history

import androidx.room.*
import hu.geri.homeguard.data.history.model.HistoryItemDisk
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryItemDao {

    @Query("SELECT * FROM histories")
    fun getHistoryItems(): Flow<List<HistoryItemDisk>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryItem(historyItem: HistoryItemDisk)

    @Query("DELETE FROM histories")
    fun deleteAllHistoryItem()
}