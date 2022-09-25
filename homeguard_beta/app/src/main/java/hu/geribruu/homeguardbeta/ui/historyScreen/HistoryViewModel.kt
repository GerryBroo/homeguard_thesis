package hu.geribruu.homeguardbeta.ui.historyScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.geribruu.homeguardbeta.data.history.HistoryRepository
import hu.geribruu.homeguardbeta.domain.history.model.HistoryItem
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
) : ViewModel() {

    val histories: LiveData<List<HistoryItem>> = repository.getHistories().asLiveData()

    fun deleteHistory() {
        viewModelScope.launch {
            repository.deleteHistories()
        }
    }
}
