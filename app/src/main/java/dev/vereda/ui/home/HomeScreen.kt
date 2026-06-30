package dev.vereda.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.ui.theme.VeredaTheme
import kotlin.math.roundToInt

/** Home screen: shows the reading streak and overall progress. */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onChooseReading: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onChooseReading = onChooseReading,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onChooseReading: () -> Unit,
    onOpenSettings: () -> Unit,
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

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StreakCard(currentStreak = state.currentStreak, bestStreak = state.bestStreak)
        ProgressCard(
            fraction = state.overallFraction,
            chaptersRead = state.chaptersRead,
            totalChapters = state.totalChapters,
        )
        Button(
            onClick = onChooseReading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Escolher leitura")
        }
        TextButton(onClick = onOpenSettings) {
            Text(text = "Ajustes")
        }
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    bestStreak: Int,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = "🔥 $currentStreak", style = MaterialTheme.typography.displayMedium)
            Text(text = "dias seguidos", style = MaterialTheme.typography.titleMedium)
            Text(text = "Melhor sequência: $bestStreak", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ProgressCard(
    fraction: Float,
    chaptersRead: Int,
    totalChapters: Int,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Progresso geral", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "$chaptersRead de $totalChapters capítulos (${(fraction * 100).roundToInt()}%)",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    VeredaTheme {
        HomeScreen(
            state =
                HomeUiState(
                    isLoading = false,
                    currentStreak = 5,
                    bestStreak = 12,
                    chaptersRead = 119,
                    totalChapters = 1189,
                ),
            onChooseReading = {},
            onOpenSettings = {},
        )
    }
}
