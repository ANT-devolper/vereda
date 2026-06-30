package dev.vereda.ui.books

import dev.vereda.data.ProgressRepository
import dev.vereda.progress.BibleBook
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val genesis = BibleBook(id = 1, name = "Gênesis", chapterCount = 50)
    private val exodus = BibleBook(id = 2, name = "Êxodo", chapterCount = 40)

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
            val viewModel = BooksViewModel(FakeProgressRepository(emptyList()))

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `loads the books with their progress`() =
        runTest {
            val books =
                listOf(
                    BookProgress(book = genesis, chaptersRead = 10),
                    BookProgress(book = exodus, chaptersRead = 0),
                )
            val viewModel = BooksViewModel(FakeProgressRepository(books))

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals(listOf("Gênesis", "Êxodo"), state.books.map { it.book.name })
            assertEquals(0.2f, state.books[0].fraction, 0.0001f)
        }
}

private class FakeProgressRepository(
    private val books: List<BookProgress>,
) : ProgressRepository {
    override suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    ) = Unit

    override suspend fun isChapterRead(
        bookId: Int,
        chapter: Int,
    ): Boolean = false

    override suspend fun readChapters(bookId: Int): Set<Int> = emptySet()

    override suspend fun bookProgress(): List<BookProgress> = books

    override suspend fun overallProgress(): OverallProgress = OverallProgress(0, 0)
}
