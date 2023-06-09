package hu.geri.homeguard.data.history

import androidx.room.*
import hu.geri.homeguard.data.face.model.RecognizedFaceDisk
import hu.geri.homeguard.data.history.model.HistoryItemDisk
import hu.geri.homeguard.domain.history.model.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryItemDao {

    @Query("SELECT * FROM histories")
    fun getHistoryItems(): Flow<List<HistoryItemDisk>>

    @Query("SELECT * FROM histories WHERE id = :id")
    fun getHistoryById(id: Int): HistoryItemDisk?

    @Query("SELECT * FROM histories WHERE name='truck'")
    fun getLatestTruck(): HistoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryItem(historyItem: HistoryItemDisk)

    @Query("DELETE FROM histories")
    fun deleteAllHistoryItem()
}