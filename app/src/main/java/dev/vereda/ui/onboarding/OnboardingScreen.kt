package dev.vereda.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.vereda.ui.settings.ReminderListEditor
import dev.vereda.ui.theme.VeredaTheme
import java.time.LocalTime

/** First-run onboarding: pick reminders and grant notification permission, then enter the app. */
@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    OnboardingScreen(
        state = state,
        onAdd = viewModel::addReminder,
        onUpdate = viewModel::updateReminder,
        onRemove = viewModel::removeReminder,
        onRequestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onFinish = viewModel::finish,
        modifier = modifier,
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onAdd: (LocalTime) -> Unit,
    onUpdate: (Int, LocalTime) -> Unit,
    onRemove: (LocalTime) -> Unit,
    onRequestPermission: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Bem-vindo ao Vereda", style = MaterialTheme.typography.headlineMedium)
        Text(
            text =
                "Leia ao menos um capítulo por dia e mantenha sua sequência. " +
                    "Defina até 3 lembretes para não esquecer.",
            style = MaterialTheme.typography.bodyMedium,
        )
        ReminderListEditor(
            reminders = state.reminders,
            canAddMore = state.canAddMore,
            onAdd = onAdd,
            onUpdate = onUpdate,
            onRemove = onRemove,
        )
        OutlinedButton(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Permitir notificações")
        }
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Começar")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    VeredaTheme {
        OnboardingScreen(
            state = OnboardingUiState(reminders = listOf(LocalTime.of(8, 0))),
            onAdd = {},
            onUpdate = { _, _ -> },
            onRemove = {},
            onRequestPermission = {},
            onFinish = {},
        )
    }
}
