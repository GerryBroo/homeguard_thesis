package hu.geri.homeguard.domain.history.model

import android.graphics.Bitmap
import hu.geri.homeguard.data.history.model.HistoryItemDisk

data class HistoryItem(
    var id: Long,
    var name: String,
    var captureDate: String,
    var bitmap: Bitmap
)

fun List<HistoryItemDisk>.toHistoryItems() = map { historyItem ->
    historyItem.toHistoryItem()
}

fun HistoryItemDisk.toHistoryItem() =
    HistoryItem(
        id = id,
        name = name,
        captureDate = captureDate,
        bitmap = bitmap
    )
