package dev.vereda.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.history.HistoryDay
import dev.vereda.history.HistoryEntry
import dev.vereda.ui.components.VeredaTopBar
import dev.vereda.ui.theme.VeredaTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Reading history: chapters the user has completed, grouped by day (most recent first). */
@Composable
fun HistoryRoute(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(state = state, onBack = onBack, modifier = modifier)
}

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        VeredaTopBar(title = "Histórico", onBack = onBack)
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.isEmpty -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Você ainda não concluiu nenhum capítulo.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(state.days, key = { it.date.toEpochDay() }) { day ->
                        DaySection(day = day)
                    }
                }
            }
        }
    }
}

@Composable
private fun DaySection(day: HistoryDay) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = formatDate(day.date), style = MaterialTheme.typography.titleMedium)
        day.entries.forEach { entry ->
            EntryRow(entry = entry)
        }
    }
}

@Composable
private fun EntryRow(entry: HistoryEntry) {
    Text(
        text = "${entry.bookName} ${entry.chapter} · ${formatTime(entry.time)}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
    )
}

private val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"))
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private fun formatDate(date: LocalDate): String = date.format(dateFormatter)

private fun formatTime(time: LocalTime): String = time.format(timeFormatter)

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    VeredaTheme {
        HistoryScreen(
            state =
                HistoryUiState(
                    isLoading = false,
                    days =
                        listOf(
                            HistoryDay(
                                date = LocalDate.of(2026, 7, 1),
                                entries =
                                    listOf(
                                        HistoryEntry(1, 1, "Gênesis", LocalTime.of(8, 0)),
                                        HistoryEntry(1, 2, "Gênesis", LocalTime.of(20, 30)),
                                    ),
                            ),
                            HistoryDay(
                                date = LocalDate.of(2026, 6, 30),
                                entries = listOf(HistoryEntry(43, 3, "João", LocalTime.of(21, 15))),
                            ),
                        ),
                ),
            onBack = {},
        )
    }
}
