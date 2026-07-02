package dev.vereda.ui.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.data.BibleReadingRepository
import dev.vereda.data.ProgressRepository
import dev.vereda.data.StreakRepository
import dev.vereda.progress.BibleCatalog
import dev.vereda.reading.ChapterTarget
import dev.vereda.reading.ReadingVerse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the reading screen. */
data class ReadingUiState(
    val isLoading: Boolean = true,
    val bookId: Int = 0,
    val bookName: String = "",
    val chapter: Int = 0,
    val verses: List<ReadingVerse> = emptyList(),
    val isCompleted: Boolean = false,
    val nextChapter: ChapterTarget? = null,
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
    private val bibleCatalog: BibleCatalog,
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
                    bookId = loaded.bookId,
                    bookName = loaded.bookName,
                    chapter = loaded.chapter,
                    verses = loaded.verses,
                    isCompleted = completed,
                    nextChapter = nextChapterAfter(loaded.bookId, loaded.chapter),
                )
        }
    }

    /**
     * The chapter that follows [chapter] in [bookId]: the next chapter of the same book, or chapter 1
     * of the next book; `null` when this is the last chapter of the last book.
     */
    private suspend fun nextChapterAfter(
        bookId: Int,
        chapter: Int,
    ): ChapterTarget? {
        val books = bibleCatalog.books()
        val index = books.indexOfFirst { it.id == bookId }
        if (index == -1) return null
        val current = books[index]
        return when {
            chapter < current.chapterCount -> ChapterTarget(bookId, chapter + 1)
            else -> books.getOrNull(index + 1)?.let { ChapterTarget(it.id, 1) }
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
