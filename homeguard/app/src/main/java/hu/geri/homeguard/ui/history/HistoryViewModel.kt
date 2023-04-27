package hu.geri.homeguard.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hu.geri.homeguard.domain.history.HistoryItemsEmptyError
import hu.geri.homeguard.domain.history.HistoryItemsSuccess
import hu.geri.homeguard.domain.history.HistoryUseCase
import hu.geri.homeguard.domain.history.model.HistoryItem
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val useCase: HistoryUseCase
) : ViewModel() {

    lateinit var historyItems: LiveData<List<HistoryItem>>

    fun loadHistory() {
        when (val result = useCase.getHistoryItems()) {
            is HistoryItemsSuccess -> {
                historyItems = result.historyItems.asLiveData()
            }
            is HistoryItemsEmptyError -> ""
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            //repository.deleteHistories()
        }
    }
}