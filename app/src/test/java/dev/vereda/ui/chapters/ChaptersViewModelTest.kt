package dev.vereda.ui.chapters

import dev.vereda.data.ProgressRepository
import dev.vereda.progress.BibleBook
import dev.vereda.progress.BibleCatalog
import dev.vereda.progress.BookProgress
import dev.vereda.progress.OverallProgress
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChaptersViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val catalog =
        object : BibleCatalog {
            override suspend fun books(): List<BibleBook> =
                listOf(
                    BibleBook(id = 1, name = "Gênesis", chapterCount = 50),
                    BibleBook(id = 2, name = "Êxodo", chapterCount = 40),
                )
        }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads the book name, chapter count and read chapters`() =
        runTest {
            val viewModel =
                ChaptersViewModel(
                    bookId = 2,
                    catalog = catalog,
                    progressRepository = FakeProgressRepository(readChapters = setOf(1, 4)),
                )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("Êxodo", state.bookName)
            assertEquals(40, state.chapterCount)
            assertEquals(setOf(1, 4), state.readChapters)
        }
}

private class FakeProgressRepository(
    private val readChapters: Set<Int>,
) : ProgressRepository {
    override suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    ) = Unit

    override suspend fun isChapterRead(
        bookId: Int,
        chapter: Int,
    ): Boolean = false

    override suspend fun readChapters(bookId: Int): Set<Int> = readChapters

    override suspend fun bookProgress(): List<BookProgress> = emptyList()

    override suspend fun overallProgress(): OverallProgress = OverallProgress(0, 0)
}
