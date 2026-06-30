package dev.vereda.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val DEFAULT_NEW_REMINDER: LocalTime = LocalTime.of(8, 0)

/**
 * Editable list of daily reminders: each entry can be edited or removed, and a new one can be added
 * (up to the cap, controlled by [canAddMore]). Shared by the settings and onboarding screens.
 */
@Composable
fun ReminderListEditor(
    reminders: List<LocalTime>,
    canAddMore: Boolean,
    onAdd: (LocalTime) -> Unit,
    onUpdate: (Int, LocalTime) -> Unit,
    onRemove: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    // null = no dialog; -1 = adding a new reminder; >= 0 = editing that index.
    var editing by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (reminders.isEmpty()) {
            Text(
                text = "Nenhum lembrete. Adicione até 3 horários para lembrar da leitura.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        reminders.forEachIndexed { index, time ->
            ReminderRow(
                time = time,
                onEdit = { editing = index },
                onRemove = { onRemove(time) },
            )
        }
        Button(
            onClick = { editing = -1 },
            enabled = canAddMore,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Adicionar lembrete")
        }
    }

    val current = editing
    if (current != null) {
        val initial = if (current >= 0) reminders[current] else DEFAULT_NEW_REMINDER
        TimePickerDialog(
            initial = initial,
            onConfirm = { time ->
                if (current >= 0) onUpdate(current, time) else onAdd(time)
                editing = null
            },
            onDismiss = { editing = null },
        )
    }
}

@Composable
private fun ReminderRow(
    time: LocalTime,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = time.format(TIME_FORMAT),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onEdit) { Text(text = "Editar") }
        TextButton(onClick = onRemove) { Text(text = "Remover") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initial: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val state =
        rememberTimePickerState(
            initialHour = initial.hour,
            initialMinute = initial.minute,
            is24Hour = true,
        )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancelar") }
        },
        text = { TimePicker(state = state) },
    )
}
