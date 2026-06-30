package dev.vereda.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.data.ProgressRepository
import dev.vereda.data.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the Home screen. */
data class HomeUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val chaptersRead: Int = 0,
    val totalChapters: Int = 0,
) {
    val overallFraction: Float
        get() = if (totalChapters == 0) 0f else chaptersRead.toFloat() / totalChapters
}

/** Exposes the reading streak and overall progress for the Home screen. */
class HomeViewModel(
    private val streakRepository: StreakRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /** Reloads streak and progress (e.g., after a chapter is completed). */
    fun refresh() {
        viewModelScope.launch {
            val streak = streakRepository.currentStreak()
            val overall = progressRepository.overallProgress()
            _uiState.value =
                HomeUiState(
                    isLoading = false,
                    currentStreak = streak.current,
                    bestStreak = streak.best,
                    chaptersRead = overall.chaptersRead,
                    totalChapters = overall.totalChapters,
                )
        }
    }
}
