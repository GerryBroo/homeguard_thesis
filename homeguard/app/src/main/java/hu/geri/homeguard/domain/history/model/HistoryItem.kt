package hu.geri.homeguard.domain.history.model

import hu.geri.homeguard.data.history.model.HistoryItemDisk

data class HistoryItem(
    var id: Long,
    var name: String,
    var captureDate: String,
)

fun List<HistoryItemDisk>.toHistoryItems() = map { historyItem ->
    historyItem.toHistoryItem()
}

fun HistoryItemDisk.toHistoryItem() =
    HistoryItem(
        id = id,
        name = name,
        captureDate = captureDate
    )
