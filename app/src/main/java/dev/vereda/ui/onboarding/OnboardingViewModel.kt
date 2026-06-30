package dev.vereda.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vereda.settings.MAX_REMINDERS
import dev.vereda.settings.OnboardingRepository
import dev.vereda.settings.ReminderEditing
import dev.vereda.settings.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

/** UI state for first-run onboarding. */
data class OnboardingUiState(
    val reminders: List<LocalTime> = listOf(LocalTime.of(8, 0)),
    val isFinished: Boolean = false,
) {
    val canAddMore: Boolean get() = reminders.size < MAX_REMINDERS
}

/** Lets the user tweak the suggested reminders on first run, then persists them and completes onboarding. */
class OnboardingViewModel(
    private val reminderRepository: ReminderRepository,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun addReminder(time: LocalTime) = setReminders(ReminderEditing.add(_uiState.value.reminders, time))

    fun updateReminder(
        index: Int,
        time: LocalTime,
    ) = setReminders(ReminderEditing.update(_uiState.value.reminders, index, time))

    fun removeReminder(time: LocalTime) = setReminders(ReminderEditing.remove(_uiState.value.reminders, time))

    /** Persists the chosen reminders and marks onboarding as completed. */
    fun finish() {
        viewModelScope.launch {
            reminderRepository.setReminders(_uiState.value.reminders)
            onboardingRepository.complete()
            _uiState.value = _uiState.value.copy(isFinished = true)
        }
    }

    private fun setReminders(updated: List<LocalTime>) {
        _uiState.value = _uiState.value.copy(reminders = updated)
    }
}
