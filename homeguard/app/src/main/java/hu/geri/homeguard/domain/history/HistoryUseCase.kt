package hu.geri.homeguard.domain.history

import hu.geri.homeguard.data.history.HistoryItemDiskDataSource
import hu.geri.homeguard.domain.history.model.HistoryItem
import hu.geri.homeguard.domain.history.model.toHistoryItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface HistoryUseCase {
    fun getHistoryItems(): HistoryResult
    fun insertHistoryItem(historyItem: HistoryItem)
    fun deleteAllHistoryItem()

}

class HistoryUseCaseImpl(
    private val dataSource: HistoryItemDiskDataSource,
) : HistoryUseCase {

    override fun getHistoryItems(): HistoryResult {
        val result = dataSource.getHistoryItems()
        //TODO check empty -> mobileo app check

        return HistoryItemsSuccess(result.map { flow ->
            flow.toHistoryItems()
        })
    }

    override fun insertHistoryItem(historyItem: HistoryItem) {
        TODO("Not yet implemented")
    }

    override fun deleteAllHistoryItem() {
        TODO("Not yet implemented")
    }

}

sealed interface HistoryResult
object HistoryItemsEmptyError : HistoryResult
data class HistoryItemsSuccess(val historyItems: Flow<List<HistoryItem>>) : HistoryResult