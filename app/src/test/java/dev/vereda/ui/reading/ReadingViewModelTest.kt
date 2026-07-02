package dev.vereda.ui.reading

import dev.vereda.data.BibleReadingRepository
import dev.vereda.data.ProgressRepository
import dev.vereda.data.StreakRepository
import dev.vereda.progress.BookProgress
import dev.vereda.progress.OverallProgress
import dev.vereda.progress.PortugueseBibleCatalog
import dev.vereda.reading.ChapterTarget
import dev.vereda.reading.ReadingChapter
import dev.vereda.reading.ReadingVerse
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
class ReadingViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val chapter =
        ReadingChapter(
            bookId = 1,
            bookName = "Gênesis",
            chapter = 1,
            verses = listOf(ReadingVerse(1, "No princípio..."), ReadingVerse(2, "E a terra...")),
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        progress: FakeProgressRepository = FakeProgressRepository(),
        streak: FakeStreakRepository = FakeStreakRepository(),
        fixture: ReadingChapter = chapter,
    ) = ReadingViewModel(
        bookId = fixture.bookId,
        chapter = fixture.chapter,
        readingRepository = FakeBibleReadingRepository(fixture),
        progressRepository = progress,
        streakRepository = streak,
        bibleCatalog = PortugueseBibleCatalog(),
    )

    @Test
    fun `loads the chapter into the state`() =
        runTest {
            val vm = viewModel()

            advanceUntilIdle()

            val state = vm.uiState.value
            assertFalse(state.isLoading)
            assertEquals("Gênesis", state.bookName)
            assertEquals(1, state.chapter)
            assertEquals(2, state.verses.size)
            assertFalse(state.isCompleted)
        }

    @Test
    fun `marking completed records progress and streak`() =
        runTest {
            val progress = FakeProgressRepository()
            val streak = FakeStreakRepository()
            val vm = viewModel(progress, streak)
            advanceUntilIdle()

            vm.markCompleted()
            advanceUntilIdle()

            assertTrue(vm.uiState.value.isCompleted)
            assertEquals(listOf(1 to 1), progress.marked)
            assertEquals(1, streak.recordedCount)
        }

    @Test
    fun `next chapter points to the following chapter of the same book`() =
        runTest {
            val vm = viewModel()

            advanceUntilIdle()

            assertEquals(ChapterTarget(bookId = 1, chapter = 2), vm.uiState.value.nextChapter)
        }

    @Test
    fun `next chapter rolls over to chapter 1 of the next book`() =
        runTest {
            val lastOfGenesis = chapter.copy(chapter = 50)
            val vm = viewModel(fixture = lastOfGenesis)

            advanceUntilIdle()

            assertEquals(ChapterTarget(bookId = 2, chapter = 1), vm.uiState.value.nextChapter)
        }

    @Test
    fun `next chapter is null at the last chapter of the last book`() =
        runTest {
            val lastOfBible =
                ReadingChapter(bookId = 66, bookName = "Apocalipse", chapter = 22, verses = chapter.verses)
            val vm = viewModel(fixture = lastOfBible)

            advanceUntilIdle()

            assertEquals(null, vm.uiState.value.nextChapter)
        }

    @Test
    fun `a chapter already read starts completed`() =
        runTest {
            val progress = FakeProgressRepository(alreadyRead = true)
            val vm = viewModel(progress = progress)

            advanceUntilIdle()

            assertTrue(vm.uiState.value.isCompleted)
        }

    @Test
    fun `marking an already completed chapter does not record again`() =
        runTest {
            val progress = FakeProgressRepository(alreadyRead = true)
            val streak = FakeStreakRepository()
            val vm = viewModel(progress = progress, streak = streak)
            advanceUntilIdle()

            vm.markCompleted()
            advanceUntilIdle()

            assertTrue(progress.marked.isEmpty())
            assertEquals(0, streak.recordedCount)
        }
}

private class FakeBibleReadingRepository(
    private val chapter: ReadingChapter,
) : BibleReadingRepository {
    override suspend fun chapter(
        bookId: Int,
        chapter: Int,
    ): ReadingChapter = this.chapter
}

private class FakeProgressRepository(
    private val alreadyRead: Boolean = false,
) : ProgressRepository {
    val marked = mutableListOf<Pair<Int, Int>>()

    override suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    ) {
        marked.add(bookId to chapter)
    }

    override suspend fun isChapterRead(
        bookId: Int,
        chapter: Int,
    ): Boolean = alreadyRead

    override suspend fun readChapters(bookId: Int): Set<Int> = emptySet()

    override suspend fun bookProgress(): List<BookProgress> = emptyList()

    override suspend fun overallProgress(): OverallProgress = OverallProgress(0, 0)
}

private class FakeStreakRepository : StreakRepository {
    var recordedCount = 0

    override suspend fun recordChapterCompleted() {
        recordedCount++
    }

    override suspend fun currentStreak(): StreakResult = StreakResult(0, 0)
}
