package dev.vereda.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.vereda.progress.BibleBook
import dev.vereda.progress.BibleCatalog
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the repository that records read chapters and reports reading progress.
 * Uses the real DAO via Robolectric (grouped query) and a fake in-memory catalog.
 */
@RunWith(AndroidJUnit4::class)
class ProgressRepositoryTest {
    private val genesis = BibleBook(id = 1, name = "Genesis", chapterCount = 50)
    private val exodus = BibleBook(id = 2, name = "Exodus", chapterCount = 40)
    private val catalog =
        object : BibleCatalog {
            override suspend fun books(): List<BibleBook> = listOf(genesis, exodus)
        }

    private lateinit var database: VeredaDatabase
    private lateinit var repository: ProgressRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VeredaDatabase::class.java).build()
        repository = DefaultProgressRepository(dao = database.chapterReadDao(), catalog = catalog)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `marking chapters is reflected in overall progress`() =
        runBlocking {
            repository.markChapterRead(bookId = 1, chapter = 1)
            repository.markChapterRead(bookId = 1, chapter = 2)
            repository.markChapterRead(bookId = 2, chapter = 1)

            val overall = repository.overallProgress()

            assertEquals(3, overall.chaptersRead)
            assertEquals(90, overall.totalChapters)
        }

    @Test
    fun `re-marking the same chapter does not double count`() =
        runBlocking {
            repository.markChapterRead(bookId = 1, chapter = 1)
            repository.markChapterRead(bookId = 1, chapter = 1)

            assertEquals(1, repository.overallProgress().chaptersRead)
        }

    @Test
    fun `isChapterRead reflects whether a chapter was marked`() =
        runBlocking {
            assertEquals(false, repository.isChapterRead(bookId = 1, chapter = 1))

            repository.markChapterRead(bookId = 1, chapter = 1)

            assertEquals(true, repository.isChapterRead(bookId = 1, chapter = 1))
            assertEquals(false, repository.isChapterRead(bookId = 1, chapter = 2))
        }

    @Test
    fun `book progress reports per-book fractions from the catalog`() =
        runBlocking {
            repeat(10) { chapter -> repository.markChapterRead(bookId = 1, chapter = chapter + 1) }

            val books = repository.bookProgress()

            assertEquals(listOf(genesis, exodus), books.map { it.book })
            assertEquals(0.2f, books[0].fraction, 0.0001f)
            assertEquals(0f, books[1].fraction, 0.0001f)
        }
}
