package dev.vereda.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.reminders.ReminderScheduler
import dev.vereda.settings.MAX_REMINDERS
import dev.vereda.settings.ReminderEditing
import dev.vereda.settings.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

/** UI state for the reminders settings screen. */
data class RemindersUiState(
    val isLoading: Boolean = true,
    val reminders: List<LocalTime> = emptyList(),
) {
    val canAddMore: Boolean get() = reminders.size < MAX_REMINDERS
}

/** Loads and edits the user's daily reminders, persisting every change. */
class RemindersViewModel(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = RemindersUiState(isLoading = false, reminders = reminderRepository.reminders())
        }
    }

    fun addReminder(time: LocalTime) = persist(ReminderEditing.add(_uiState.value.reminders, time))

    fun updateReminder(
        index: Int,
        time: LocalTime,
    ) = persist(ReminderEditing.update(_uiState.value.reminders, index, time))

    fun removeReminder(time: LocalTime) = persist(ReminderEditing.remove(_uiState.value.reminders, time))

    private fun persist(updated: List<LocalTime>) {
        _uiState.value = _uiState.value.copy(reminders = updated)
        viewModelScope.launch {
            reminderRepository.setReminders(updated)
            reminderScheduler.schedule(updated)
        }
    }
}
