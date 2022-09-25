package hu.geribruu.homeguardbeta.data.history.disk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.geribruu.homeguardbeta.data.history.disk.model.RoomHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * from histories")
    fun getHistories(): Flow<List<RoomHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryItem(historyItem: RoomHistoryItem)

    @Query("DELETE FROM histories")
    suspend fun deleteHistory()
}
