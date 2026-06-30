package dev.vereda.ui.chapters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.data.ProgressRepository
import dev.vereda.progress.BibleCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the chapter grid of a single book. */
data class ChaptersUiState(
    val isLoading: Boolean = true,
    val bookName: String = "",
    val chapterCount: Int = 0,
    val readChapters: Set<Int> = emptySet(),
)

/** Exposes a book's chapters and which of them have already been read. */
class ChaptersViewModel(
    private val bookId: Int,
    private val catalog: BibleCatalog,
    private val progressRepository: ProgressRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChaptersUiState())
    val uiState: StateFlow<ChaptersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val book = catalog.books().first { it.id == bookId }
            _uiState.value =
                ChaptersUiState(
                    isLoading = false,
                    bookName = book.name,
                    chapterCount = book.chapterCount,
                    readChapters = progressRepository.readChapters(bookId),
                )
        }
    }
}
