package dev.vereda.ui.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.data.BibleReadingRepository
import dev.vereda.data.ProgressRepository
import dev.vereda.data.StreakRepository
import dev.vereda.reading.ReadingVerse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the reading screen. */
data class ReadingUiState(
    val isLoading: Boolean = true,
    val bookName: String = "",
    val chapter: Int = 0,
    val verses: List<ReadingVerse> = emptyList(),
    val isCompleted: Boolean = false,
)

/**
 * Loads a chapter for reading and records its completion (reading progress + daily streak).
 *
 * Completion is idempotent: marking an already-read chapter is a no-op so the streak is not bumped twice.
 */
class ReadingViewModel(
    private val bookId: Int,
    private val chapter: Int,
    private val readingRepository: BibleReadingRepository,
    private val progressRepository: ProgressRepository,
    private val streakRepository: StreakRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingUiState())
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val loaded = readingRepository.chapter(bookId, chapter)
            val completed = progressRepository.isChapterRead(bookId, chapter)
            _uiState.value =
                ReadingUiState(
                    isLoading = false,
                    bookName = loaded.bookName,
                    chapter = loaded.chapter,
                    verses = loaded.verses,
                    isCompleted = completed,
                )
        }
    }

    /** Records the chapter as completed, updating reading progress and the daily streak. */
    fun markCompleted() {
        if (_uiState.value.isCompleted) return
        viewModelScope.launch {
            progressRepository.markChapterRead(bookId, chapter)
            streakRepository.recordChapterCompleted()
            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }
}
