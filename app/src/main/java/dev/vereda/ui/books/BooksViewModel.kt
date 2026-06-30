package dev.vereda.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.data.ProgressRepository
import dev.vereda.progress.BookProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the book list (Bible navigation). */
data class BooksUiState(
    val isLoading: Boolean = true,
    val books: List<BookProgress> = emptyList(),
)

/** Exposes the catalog of books with their reading progress. */
class BooksViewModel(
    private val progressRepository: ProgressRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = BooksUiState(isLoading = false, books = progressRepository.bookProgress())
        }
    }
}
