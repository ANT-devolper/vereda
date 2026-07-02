package dev.vereda.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.data.ReadingHistoryRepository
import dev.vereda.history.HistoryDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the reading history screen. */
data class HistoryUiState(
    val isLoading: Boolean = true,
    val days: List<HistoryDay> = emptyList(),
) {
    val isEmpty: Boolean
        get() = !isLoading && days.isEmpty()
}

/** Exposes the day-by-day reading history for the history screen. */
class HistoryViewModel(
    private val readingHistoryRepository: ReadingHistoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /** Reloads the history from storage. */
    fun refresh() {
        viewModelScope.launch {
            val days = readingHistoryRepository.history()
            _uiState.value = HistoryUiState(isLoading = false, days = days)
        }
    }
}
