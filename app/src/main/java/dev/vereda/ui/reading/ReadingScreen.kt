package dev.vereda.ui.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.R
import dev.vereda.reading.ChapterTarget
import dev.vereda.reading.ReadingVerse
import dev.vereda.ui.components.VeredaTopBar
import dev.vereda.ui.theme.Dimens
import dev.vereda.ui.theme.ReadingTextStyle
import dev.vereda.ui.theme.VeredaTheme

/** Reading screen: shows a chapter's verses and a dynamic complete / next-chapter button at the end. */
@Composable
fun ReadingRoute(
    viewModel: ReadingViewModel,
    onBack: () -> Unit,
    onNavigateToChapter: (bookId: Int, chapter: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReadingScreen(
        state = state,
        onBack = onBack,
        onComplete = viewModel::markCompleted,
        onNext = { target -> onNavigateToChapter(target.bookId, target.chapter) },
        modifier = modifier,
    )
}

@Composable
fun ReadingScreen(
    state: ReadingUiState,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onNext: (ChapterTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        VeredaTopBar(title = "${state.bookName} ${state.chapter}", onBack = onBack)
        // A fresh scroll state per chapter so navigating to the next chapter starts at the top.
        val listState = key(state.bookId, state.chapter) { rememberLazyListState() }
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.gap),
        ) {
            item { Spacer(Modifier.height(Dimens.gapSmall)) }
            items(state.verses, key = { it.number }) { verse ->
                VerseItem(verse)
            }
            item {
                CompletionButton(
                    isCompleted = state.isCompleted,
                    nextChapter = state.nextChapter,
                    onComplete = onComplete,
                    onNext = onNext,
                    modifier = Modifier.padding(vertical = Dimens.gapLarge),
                )
            }
        }
    }
}

@Composable
private fun VerseItem(verse: ReadingVerse) {
    Text(
        text =
            buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        baselineShift = BaselineShift.Superscript,
                        color = MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    append("${verse.number} ")
                }
                append(verse.text)
            },
        style = ReadingTextStyle,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun CompletionButton(
    isCompleted: Boolean,
    nextChapter: ChapterTarget?,
    onComplete: () -> Unit,
    onNext: (ChapterTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonModifier = modifier.fillMaxWidth().height(56.dp)
    when {
        !isCompleted -> {
            Button(onClick = onComplete, modifier = buttonModifier) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(Dimens.gapSmall))
                Text(text = "Marcar como concluído", style = MaterialTheme.typography.titleMedium)
            }
        }

        nextChapter != null -> {
            Button(onClick = { onNext(nextChapter) }, modifier = buttonModifier) {
                Text(text = "Próximo capítulo", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(Dimens.gapSmall))
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        else -> {
            Button(
                onClick = {},
                enabled = false,
                modifier = buttonModifier,
                colors =
                    ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.secondary,
                    ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(Dimens.gapSmall))
                Text(text = "Concluído", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1419)
@Composable
private fun ReadingScreenPreview() {
    VeredaTheme {
        ReadingScreen(
            state =
                ReadingUiState(
                    isLoading = false,
                    bookId = 1,
                    bookName = "Gênesis",
                    chapter = 1,
                    verses =
                        listOf(
                            ReadingVerse(1, "No princípio criou Deus os céus e a terra."),
                            ReadingVerse(2, "E a terra era sem forma e vazia; e havia trevas sobre a face do abismo."),
                        ),
                    isCompleted = false,
                    nextChapter = ChapterTarget(bookId = 1, chapter = 2),
                ),
            onBack = {},
            onComplete = {},
            onNext = {},
        )
    }
}
