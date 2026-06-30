package dev.vereda.ui.chapters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.ui.theme.VeredaTheme

/** Chapter grid for a book: every chapter, marked read/unread, tappable to open the reading screen. */
@Composable
fun ChaptersRoute(
    viewModel: ChaptersViewModel,
    onChapterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChaptersScreen(state = state, onChapterClick = onChapterClick, modifier = modifier)
}

@Composable
fun ChaptersScreen(
    state: ChaptersUiState,
    onChapterClick: (Int) -> Unit,
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

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = state.bookName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 64.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items((1..state.chapterCount).toList(), key = { it }) { chapter ->
                ChapterCell(
                    chapter = chapter,
                    isRead = chapter in state.readChapters,
                    onClick = { onChapterClick(chapter) },
                )
            }
        }
    }
}

@Composable
private fun ChapterCell(
    chapter: Int,
    isRead: Boolean,
    onClick: () -> Unit,
) {
    val colors =
        if (isRead) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            CardDefaults.cardColors()
        }
    Card(
        onClick = onClick,
        colors = colors,
        modifier = Modifier.aspectRatio(1f),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = chapter.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isRead) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChaptersScreenPreview() {
    VeredaTheme {
        ChaptersScreen(
            state =
                ChaptersUiState(
                    isLoading = false,
                    bookName = "Gênesis",
                    chapterCount = 12,
                    readChapters = setOf(1, 2, 5),
                ),
            onChapterClick = {},
        )
    }
}
