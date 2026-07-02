package dev.vereda.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import dev.vereda.ui.components.VeredaTopBar
import dev.vereda.ui.theme.VeredaTheme
import java.time.LocalTime

/** Settings screen for managing the user's daily reminders. */
@Composable
fun RemindersRoute(
    viewModel: RemindersViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RemindersScreen(
        state = state,
        onBack = onBack,
        onAdd = viewModel::addReminder,
        onUpdate = viewModel::updateReminder,
        onRemove = viewModel::removeReminder,
        modifier = modifier,
    )
}

@Composable
fun RemindersScreen(
    state: RemindersUiState,
    onBack: () -> Unit,
    onAdd: (LocalTime) -> Unit,
    onUpdate: (Int, LocalTime) -> Unit,
    onRemove: (LocalTime) -> Unit,
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
        VeredaTopBar(title = "Lembretes", onBack = onBack)
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Defina até 3 horários para lembrar da leitura diária.",
                style = MaterialTheme.typography.bodyMedium,
            )
            ReminderListEditor(
                reminders = state.reminders,
                canAddMore = state.canAddMore,
                onAdd = onAdd,
                onUpdate = onUpdate,
                onRemove = onRemove,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RemindersScreenPreview() {
    VeredaTheme {
        RemindersScreen(
            state =
                RemindersUiState(
                    isLoading = false,
                    reminders = listOf(LocalTime.of(8, 0), LocalTime.of(20, 30)),
                ),
            onBack = {},
            onAdd = {},
            onUpdate = { _, _ -> },
            onRemove = {},
        )
    }
}
