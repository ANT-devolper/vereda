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
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Tests for the repository that derives the reading history from recorded chapters.
 * Uses the real DAO via Robolectric with a fixed UTC clock and a fake catalog.
 */
@RunWith(AndroidJUnit4::class)
class ReadingHistoryRepositoryTest {
    private val genesis = BibleBook(id = 1, name = "Gênesis", chapterCount = 50)
    private val exodus = BibleBook(id = 2, name = "Êxodo", chapterCount = 40)
    private val catalog =
        object : BibleCatalog {
            override suspend fun books(): List<BibleBook> = listOf(genesis, exodus)
        }
    private val clock = Clock.fixed(Instant.parse("2026-07-01T12:00:00Z"), ZoneOffset.UTC)

    private lateinit var database: VeredaDatabase
    private lateinit var dao: ChapterReadDao
    private lateinit var repository: ReadingHistoryRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VeredaDatabase::class.java).build()
        dao = database.chapterReadDao()
        repository = DefaultReadingHistoryRepository(dao = dao, catalog = catalog, clock = clock)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `no reads yields an empty history`() =
        runBlocking {
            assertEquals(emptyList<Any>(), repository.history())
        }

    @Test
    fun `groups reads by day with resolved names, newest day first`() =
        runBlocking {
            dao.markRead(ChapterRead(1, 1, Instant.parse("2026-06-30T08:00:00Z")))
            dao.markRead(ChapterRead(1, 2, Instant.parse("2026-07-01T09:00:00Z")))
            dao.markRead(ChapterRead(2, 1, Instant.parse("2026-07-01T21:00:00Z")))

            val history = repository.history()

            assertEquals(listOf(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 6, 30)), history.map { it.date })

            val firstDay = history[0]
            assertEquals(listOf("Gênesis", "Êxodo"), firstDay.entries.map { it.bookName })
            assertEquals(listOf(2, 1), firstDay.entries.map { it.chapter })

            val secondDay = history[1]
            assertEquals("Gênesis", secondDay.entries.single().bookName)
            assertEquals(1, secondDay.entries.single().chapter)
        }
}
