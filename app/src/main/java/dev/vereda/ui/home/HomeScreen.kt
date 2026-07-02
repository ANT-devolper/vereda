package dev.vereda.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.R
import dev.vereda.ui.theme.Dimens
import dev.vereda.ui.theme.VeredaTheme
import kotlin.math.roundToInt

/** Home screen: shows the reading streak and overall progress. */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onChooseReading: () -> Unit,
    onViewHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onChooseReading = onChooseReading,
        onViewHistory = onViewHistory,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onChooseReading: () -> Unit,
    onViewHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.gapLarge),
    ) {
        Header()
        StreakCard(
            currentStreak = state.currentStreak,
            bestStreak = state.bestStreak,
            hasReadToday = state.hasReadToday,
        )
        ProgressCard(
            fraction = state.overallFraction,
            chaptersRead = state.chaptersRead,
            totalChapters = state.totalChapters,
        )
        Button(
            onClick = onChooseReading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_menu_book),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(Dimens.gapSmall))
            Text(text = "Escolher leitura", style = MaterialTheme.typography.titleMedium)
        }
        NavRow(
            iconRes = R.drawable.ic_history,
            label = "Histórico",
            onClick = onViewHistory,
        )
        NavRow(
            iconRes = R.drawable.ic_settings,
            label = "Ajustes",
            onClick = onOpenSettings,
        )
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "Vereda",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Um capítulo por dia, um passo no caminho.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    bestStreak: Int,
    hasReadToday: Boolean,
) {
    val gold = MaterialTheme.colorScheme.secondary
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, gold.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Dimens.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_flame),
                    contentDescription = null,
                    // Keep the drawable's own colors (reddish flame + amber highlight).
                    tint = Color.Unspecified,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.width(Dimens.gapSmall))
                Text(
                    text = currentStreak.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Text(
                text = if (currentStreak == 1) "dia seguido" else "dias seguidos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Pill(text = "Melhor sequência: $bestStreak")
            Text(
                text = if (hasReadToday) "Você já leu hoje ✓" else "Você ainda não leu hoje",
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasReadToday) gold else MaterialTheme.colorScheme.outline,
                fontWeight = if (hasReadToday) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun Pill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ProgressCard(
    fraction: Float,
    chaptersRead: Int,
    totalChapters: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.gapSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "Progresso geral",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "${(fraction * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            LinearProgressIndicator(
                progress = { fraction },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "$chaptersRead de $totalChapters capítulos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun NavRow(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.cardPadding, vertical = Dimens.gap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(Dimens.gap))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1419)
@Composable
private fun HomeScreenPreview() {
    VeredaTheme {
        Surface(color = Color(0xFF0F1419)) {
            HomeScreen(
                state =
                    HomeUiState(
                        isLoading = false,
                        currentStreak = 5,
                        bestStreak = 12,
                        chaptersRead = 119,
                        totalChapters = 1189,
                        hasReadToday = true,
                    ),
                onChooseReading = {},
                onViewHistory = {},
                onOpenSettings = {},
            )
        }
    }
}
