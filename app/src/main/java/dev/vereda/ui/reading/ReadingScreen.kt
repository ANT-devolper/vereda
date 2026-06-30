package dev.vereda.ui.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.reading.ReadingVerse
import dev.vereda.ui.theme.VeredaTheme

/** Reading screen: shows a chapter's verses and a "mark as completed" button at the end. */
@Composable
fun ReadingRoute(
    viewModel: ReadingViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReadingScreen(state = state, onComplete = viewModel::markCompleted, modifier = modifier)
}

@Composable
fun ReadingScreen(
    state: ReadingUiState,
    onComplete: () -> Unit,
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
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "${state.bookName} ${state.chapter}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }
        items(state.verses, key = { it.number }) { verse ->
            VerseItem(verse)
        }
        item {
            CompletionButton(
                isCompleted = state.isCompleted,
                onComplete = onComplete,
                modifier = Modifier.padding(vertical = 24.dp),
            )
        }
    }
}

@Composable
private fun VerseItem(verse: ReadingVerse) {
    Text(
        text =
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp)) {
                    append("${verse.number} ")
                }
                append(verse.text)
            },
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun CompletionButton(
    isCompleted: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onComplete,
        enabled = !isCompleted,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(text = if (isCompleted) "Concluído ✓" else "Marcar como concluído")
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingScreenPreview() {
    VeredaTheme {
        ReadingScreen(
            state =
                ReadingUiState(
                    isLoading = false,
                    bookName = "Gênesis",
                    chapter = 1,
                    verses =
                        listOf(
                            ReadingVerse(1, "No princípio criou Deus os céus e a terra."),
                            ReadingVerse(2, "E a terra era sem forma e vazia; e havia trevas sobre a face do abismo."),
                        ),
                    isCompleted = false,
                ),
            onComplete = {},
        )
    }
}
