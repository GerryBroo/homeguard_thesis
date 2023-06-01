package hu.geri.homeguard.domain.truck

import android.annotation.SuppressLint
import hu.geri.homeguard.data.history.HistoryItemDiskDataSource
import hu.geri.homeguard.domain.history.HistoryUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

interface TruckUseCase {
    fun handleTruckDetection()
}

@SuppressLint("SimpleDateFormat")
@OptIn(DelicateCoroutinesApi::class)
class TruckUseCaseImpl(
    private val historyDataSource: HistoryItemDiskDataSource,
    private val historyUseCase: HistoryUseCase
) : TruckUseCase {

    private var truckDetectionIsEnable = true

    init {
        checkIfDetectionIsEnable()
    }

    private fun checkIfDetectionIsEnable() {
        GlobalScope.launch {
            val result = historyDataSource.getLatestTruck()
            if (result != null) {
                val day = SimpleDateFormat(
                    DAY_FORMAT
                ).format(System.currentTimeMillis())

                val date = SimpleDateFormat(FILENAME_FORMAT).parse(result.captureDate)
                val resultDay = date?.let {
                    SimpleDateFormat(
                        DAY_FORMAT
                    ).format(it)
                }
                if (day == resultDay) {
                    truckDetectionIsEnable = false
                }
            }
        }
    }

    override fun handleTruckDetection() {
        if (truckDetectionIsEnable) {
            checkIfDetectionIsEnable()
            historyUseCase.insertHistoryItem("truck")
        }
    }

    companion object {
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val DAY_FORMAT = "dd"
    }
}