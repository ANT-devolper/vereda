package dev.vereda.ui.history

import dev.vereda.data.ReadingHistoryRepository
import dev.vereda.history.HistoryDay
import dev.vereda.history.HistoryEntry
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
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val sampleDay =
        HistoryDay(
            date = LocalDate.of(2026, 7, 1),
            entries = listOf(HistoryEntry(bookId = 1, chapter = 1, bookName = "Gênesis", time = LocalTime.of(8, 0))),
        )

    @Test
    fun `starts in a loading state`() =
        runTest {
            val viewModel = HistoryViewModel(FakeReadingHistoryRepository(listOf(sampleDay)))

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `loads the grouped history into the state`() =
        runTest {
            val viewModel = HistoryViewModel(FakeReadingHistoryRepository(listOf(sampleDay)))

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals(listOf(sampleDay), state.days)
            assertFalse(state.isEmpty)
        }

    @Test
    fun `an empty history is reported as empty once loaded`() =
        runTest {
            val viewModel = HistoryViewModel(FakeReadingHistoryRepository(emptyList()))

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue(state.isEmpty)
        }
}

private class FakeReadingHistoryRepository(
    private val days: List<HistoryDay>,
) : ReadingHistoryRepository {
    override suspend fun history(): List<HistoryDay> = days
}
