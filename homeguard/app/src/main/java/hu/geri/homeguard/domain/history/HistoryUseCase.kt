package hu.geri.homeguard.domain.history

import android.graphics.Bitmap
import hu.geri.homeguard.data.history.HistoryItemDiskDataSource
import hu.geri.homeguard.data.history.model.HistoryEnum
import hu.geri.homeguard.data.history.model.HistoryItemDisk
import hu.geri.homeguard.domain.history.model.HistoryItem
import hu.geri.homeguard.domain.history.model.toHistoryItems
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

interface HistoryUseCase {
    fun getHistoryItems(): HistoryResult
    suspend fun getHistoryItemById(id: Int): HistoryItemDisk?
    fun insertFaceHistoryItem(
        name: String,
        bitmap: Bitmap,
        embeedings: Array<FloatArray>,
        type: HistoryEnum
    )
    fun insertTruckHistoryItem()
    suspend fun deleteAllHistoryItem()
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

    override suspend fun getHistoryItemById(id: Int): HistoryItemDisk? {
        return dataSource.getHistoryById(id)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun insertFaceHistoryItem(
        name: String,
        bitmap: Bitmap,
        embeedings: Array<FloatArray>,
        type: HistoryEnum
    ) {
        GlobalScope.launch {
            val date =
                SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
            dataSource.insertHistoryItem(HistoryItemDisk(0, name, date, bitmap, embeedings, type))
        }
    }

    override fun insertTruckHistoryItem() {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun deleteAllHistoryItem() {
        GlobalScope.launch {
            dataSource.deleteAllHistoryItem()
        }
    }

    companion object {
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

sealed interface HistoryResult
object HistoryItemsEmptyError : HistoryResult
data class HistoryItemsSuccess(val historyItems: Flow<List<HistoryItem>>) : HistoryResult