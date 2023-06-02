package hu.geri.homeguard.ui.history

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.data.history.model.HistoryEnum
import hu.geri.homeguard.domain.analyzer.model.AddFaceData
import hu.geri.homeguard.domain.face.FaceUseCase
import hu.geri.homeguard.domain.history.HistoryItemsEmptyError
import hu.geri.homeguard.domain.history.HistoryItemsSuccess
import hu.geri.homeguard.domain.history.HistoryUseCase
import hu.geri.homeguard.domain.history.model.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val useCaseHistory: HistoryUseCase,
    private val faceUseCase: FaceUseCase
) : ViewModel() {

    lateinit var historyItems: LiveData<List<HistoryItem>>

    private lateinit var faceData: AddFaceData

    fun loadHistory() {
        when (val result = useCaseHistory.getHistoryItems()) {
            is HistoryItemsSuccess -> {
                historyItems = result.historyItems.asLiveData()
            }
            is HistoryItemsEmptyError -> ""
        }
    }

    fun getHistoryBitmap(id: Int, callback: BitmapCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val historyData = useCaseHistory.getHistoryItemById(id)
            if (historyData != null && historyData.type == HistoryEnum.UNKNOWN_FACE) {
                faceData = AddFaceData(
                    historyData.bitmap,
                    historyData.embeedings,
                    "",
                    historyData.type
                )
                if (faceData.bitmap != null) {
                    callback.onBitmapLoaded(faceData.bitmap!!)
                } else {
                    callback.onBitmapLoadError()
                }
            }
        }
    }

    fun savePostman() {
        viewModelScope.launch(Dispatchers.IO) {
            faceData.embeedings?.let { faceUseCase.saveFace("Postman", it, "") }
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            useCaseHistory.deleteAllHistoryItem()
        }
    }
}

interface BitmapCallback {
    fun onBitmapLoaded(bitmap: Bitmap)
    fun onBitmapLoadError()
}