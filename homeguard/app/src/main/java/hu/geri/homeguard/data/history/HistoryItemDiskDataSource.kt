package hu.geri.homeguard.data.history

import hu.geri.homeguard.data.history.model.HistoryItemDisk
import kotlinx.coroutines.flow.Flow

class HistoryItemDiskDataSource(
    private val dao: HistoryItemDao,
) {

    fun getHistoryItems(): Flow<List<HistoryItemDisk>> {
        return dao.getHistoryItems()
    }

    suspend fun insertHistoryItem(historyItem: HistoryItemDisk) {
        dao.insertHistoryItem(historyItem)
    }

    suspend fun deleteHistoryItem(historyItem: HistoryItemDisk) {
        dao.deleteHistoryItem(historyItem)
    }
}