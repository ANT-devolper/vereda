package dev.vereda.ui.onboarding

import dev.vereda.settings.OnboardingRepository
import dev.vereda.settings.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        reminders: FakeReminderRepository = FakeReminderRepository(),
        onboarding: FakeOnboardingRepository = FakeOnboardingRepository(),
    ) = OnboardingViewModel(reminders, onboarding)

    @Test
    fun `starts with a single suggested reminder at 8am`() {
        val state = viewModel().uiState.value

        assertEquals(listOf(LocalTime.of(8, 0)), state.reminders)
        assertFalse(state.isFinished)
    }

    @Test
    fun `edits the suggested reminders before finishing`() {
        val viewModel = viewModel()

        viewModel.addReminder(LocalTime.of(20, 0))
        viewModel.updateReminder(index = 0, time = LocalTime.of(7, 0))
        viewModel.removeReminder(LocalTime.of(20, 0))

        assertEquals(listOf(LocalTime.of(7, 0)), viewModel.uiState.value.reminders)
    }

    @Test
    fun `finishing persists the reminders and marks onboarding complete`() =
        runTest {
            val reminders = FakeReminderRepository()
            val onboarding = FakeOnboardingRepository()
            val viewModel = viewModel(reminders, onboarding)
            viewModel.addReminder(LocalTime.of(20, 0))

            viewModel.finish()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isFinished)
            assertEquals(listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)), reminders.stored)
            assertTrue(onboarding.completed)
        }
}

private class FakeReminderRepository(
    var stored: MutableList<LocalTime> = mutableListOf(),
) : ReminderRepository {
    override suspend fun reminders(): List<LocalTime> = stored.toList()

    override suspend fun setReminders(times: List<LocalTime>) {
        stored = times.toMutableList()
    }
}

private class FakeOnboardingRepository : OnboardingRepository {
    var completed = false

    override suspend fun isCompleted(): Boolean = completed

    override suspend fun complete() {
        completed = true
    }
}
