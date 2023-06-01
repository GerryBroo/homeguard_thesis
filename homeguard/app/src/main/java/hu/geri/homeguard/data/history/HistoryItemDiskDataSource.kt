package hu.geri.homeguard.data.history

import hu.geri.homeguard.data.history.model.HistoryItemDisk
import hu.geri.homeguard.domain.history.model.HistoryItem
import kotlinx.coroutines.flow.Flow

class HistoryItemDiskDataSource(
    private val dao: HistoryItemDao,
) {

    fun getHistoryItems(): Flow<List<HistoryItemDisk>> {
        return dao.getHistoryItems()
    }

    fun getLatestTruck(): HistoryItem? {
        return dao.getLatestTruck()
    }

    fun insertHistoryItem(historyItem: HistoryItemDisk) {
        dao.insertHistoryItem(historyItem)
    }

    fun deleteAllHistoryItem() {
        dao.deleteAllHistoryItem()
    }
}