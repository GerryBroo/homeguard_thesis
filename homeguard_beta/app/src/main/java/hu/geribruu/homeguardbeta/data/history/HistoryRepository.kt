package hu.geribruu.homeguardbeta.data.history

import hu.geribruu.homeguardbeta.data.history.disk.HistoryDao
import hu.geribruu.homeguardbeta.data.history.disk.model.RoomHistoryItem
import hu.geribruu.homeguardbeta.domain.history.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val dao: HistoryDao,
) {
    fun getHistories(): Flow<List<HistoryItem>> {
        return dao.getHistories().map { flow ->
            flow.map { history ->
                HistoryItem(
                    id = history.id,
                    name = history.name,
                    captureDate = history.captureDate
                )
            }
        }
    }

    fun insertHistory(historyItem: HistoryItem) {
        dao.insertHistoryItem(
            RoomHistoryItem(
                historyItem.id,
                historyItem.name,
                historyItem.captureDate
            )
        )
    }

    fun deleteHistories() {
        dao.deleteHistory()
    }
}
