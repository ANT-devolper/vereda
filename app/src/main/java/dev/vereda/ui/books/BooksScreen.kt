package dev.vereda.ui.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.progress.BibleBook
import dev.vereda.progress.BookProgress
import dev.vereda.ui.theme.VeredaTheme

/** Book list: every book of the Bible with its reading progress, tappable to open its chapters. */
@Composable
fun BooksRoute(
    viewModel: BooksViewModel,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BooksScreen(state = state, onBookClick = onBookClick, modifier = modifier)
}

@Composable
fun BooksScreen(
    state: BooksUiState,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.books, key = { it.book.id }) { book ->
            BookRow(book = book, onClick = { onBookClick(book.book.id) })
        }
    }
}

@Composable
private fun BookRow(
    book: BookProgress,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = book.book.name, style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(
            progress = { book.fraction },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "${book.chaptersRead}/${book.book.chapterCount} capítulos",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BooksScreenPreview() {
    VeredaTheme {
        BooksScreen(
            state =
                BooksUiState(
                    isLoading = false,
                    books =
                        listOf(
                            BookProgress(BibleBook(1, "Gênesis", 50), chaptersRead = 10),
                            BookProgress(BibleBook(2, "Êxodo", 40), chaptersRead = 0),
                        ),
                ),
            onBookClick = {},
        )
    }
}
