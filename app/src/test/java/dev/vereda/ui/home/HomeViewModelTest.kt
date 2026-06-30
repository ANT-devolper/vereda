package dev.vereda.ui.home

import dev.vereda.data.ProgressRepository
import dev.vereda.data.StreakRepository
import dev.vereda.progress.BookProgress
import dev.vereda.progress.OverallProgress
import dev.vereda.streak.StreakResult
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

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
            val viewModel =
                HomeViewModel(
                    streakRepository = FakeStreakRepository(StreakResult(current = 0, best = 0)),
                    progressRepository = FakeProgressRepository(OverallProgress(0, 1189)),
                )

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `loads streak and overall progress into the state`() =
        runTest {
            val viewModel =
                HomeViewModel(
                    streakRepository = FakeStreakRepository(StreakResult(current = 3, best = 7)),
                    progressRepository = FakeProgressRepository(OverallProgress(chaptersRead = 119, totalChapters = 1189)),
                )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals(3, state.currentStreak)
            assertEquals(7, state.bestStreak)
            assertEquals(119, state.chaptersRead)
            assertEquals(1189, state.totalChapters)
            assertEquals(119f / 1189f, state.overallFraction, 0.0001f)
        }
}

private class FakeStreakRepository(
    private val result: StreakResult,
) : StreakRepository {
    override suspend fun recordChapterCompleted() = Unit

    override suspend fun currentStreak(): StreakResult = result
}

private class FakeProgressRepository(
    private val overall: OverallProgress,
    private val books: List<BookProgress> = emptyList(),
) : ProgressRepository {
    override suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    ) = Unit

    override suspend fun bookProgress(): List<BookProgress> = books

    override suspend fun overallProgress(): OverallProgress = overall
}
