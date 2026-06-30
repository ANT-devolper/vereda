package dev.vereda.ui.settings

import dev.vereda.reminders.ReminderScheduler
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
class RemindersViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scheduler = FakeReminderScheduler()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts in a loading state`() =
        runTest {
            val viewModel = RemindersViewModel(FakeReminderRepository(), scheduler)

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `loads the stored reminders`() =
        runTest {
            val viewModel =
                RemindersViewModel(
                    FakeReminderRepository(mutableListOf(LocalTime.of(8, 0), LocalTime.of(20, 0))),
                    scheduler,
                )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals(listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)), state.reminders)
            assertTrue(state.canAddMore)
        }

    @Test
    fun `adding reminders persists them and caps at three`() =
        runTest {
            val repository = FakeReminderRepository()
            val viewModel = RemindersViewModel(repository, scheduler)
            advanceUntilIdle()

            viewModel.addReminder(LocalTime.of(9, 0))
            viewModel.addReminder(LocalTime.of(7, 0))
            viewModel.addReminder(LocalTime.of(18, 0))
            viewModel.addReminder(LocalTime.of(22, 0))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(listOf(LocalTime.of(7, 0), LocalTime.of(9, 0), LocalTime.of(18, 0)), state.reminders)
            assertFalse(state.canAddMore)
            assertEquals(listOf(LocalTime.of(7, 0), LocalTime.of(9, 0), LocalTime.of(18, 0)), repository.stored)
            assertEquals(listOf(LocalTime.of(7, 0), LocalTime.of(9, 0), LocalTime.of(18, 0)), scheduler.scheduled)
        }

    @Test
    fun `removing a reminder persists the change`() =
        runTest {
            val repository = FakeReminderRepository(mutableListOf(LocalTime.of(8, 0), LocalTime.of(20, 0)))
            val viewModel = RemindersViewModel(repository, scheduler)
            advanceUntilIdle()

            viewModel.removeReminder(LocalTime.of(8, 0))
            advanceUntilIdle()

            assertEquals(listOf(LocalTime.of(20, 0)), viewModel.uiState.value.reminders)
            assertEquals(listOf(LocalTime.of(20, 0)), repository.stored)
        }

    @Test
    fun `updating a reminder persists the change`() =
        runTest {
            val repository = FakeReminderRepository(mutableListOf(LocalTime.of(8, 0), LocalTime.of(20, 0)))
            val viewModel = RemindersViewModel(repository, scheduler)
            advanceUntilIdle()

            viewModel.updateReminder(index = 0, time = LocalTime.of(22, 0))
            advanceUntilIdle()

            assertEquals(listOf(LocalTime.of(20, 0), LocalTime.of(22, 0)), viewModel.uiState.value.reminders)
            assertEquals(listOf(LocalTime.of(20, 0), LocalTime.of(22, 0)), repository.stored)
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

private class FakeReminderScheduler : ReminderScheduler {
    var scheduled: List<LocalTime> = emptyList()

    override fun schedule(times: List<LocalTime>) {
        scheduled = times
    }

    override fun cancelAll() {
        scheduled = emptyList()
    }
}
