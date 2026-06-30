package dev.vereda.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

/**
 * DAO tests for read chapters, run on the JVM via Robolectric with an in-memory database.
 * Distinct read chapters per book are the source for reading progress.
 */
@RunWith(AndroidJUnit4::class)
class ChapterReadDaoTest {
    private lateinit var database: VeredaDatabase
    private lateinit var dao: ChapterReadDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VeredaDatabase::class.java).build()
        dao = database.chapterReadDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `marking chapters counts distinct reads`() =
        runBlocking {
            dao.markRead(ChapterRead(bookId = 1, chapter = 1, firstReadAt = Instant.ofEpochMilli(1000)))
            dao.markRead(ChapterRead(bookId = 1, chapter = 2, firstReadAt = Instant.ofEpochMilli(2000)))

            assertEquals(2, dao.totalChaptersRead())
        }

    @Test
    fun `re-marking the same chapter keeps the first read timestamp`() =
        runBlocking {
            dao.markRead(ChapterRead(bookId = 1, chapter = 1, firstReadAt = Instant.ofEpochMilli(1000)))
            dao.markRead(ChapterRead(bookId = 1, chapter = 1, firstReadAt = Instant.ofEpochMilli(9000)))

            assertEquals(1, dao.totalChaptersRead())
            assertEquals(Instant.ofEpochMilli(1000), dao.getFirstReadAt(bookId = 1, chapter = 1))
        }

    @Test
    fun `read counts are grouped by book`() =
        runBlocking {
            dao.markRead(ChapterRead(bookId = 1, chapter = 1, firstReadAt = Instant.ofEpochMilli(1000)))
            dao.markRead(ChapterRead(bookId = 1, chapter = 2, firstReadAt = Instant.ofEpochMilli(2000)))
            dao.markRead(ChapterRead(bookId = 2, chapter = 1, firstReadAt = Instant.ofEpochMilli(3000)))

            val counts = dao.readCountsByBook().associate { it.bookId to it.chaptersRead }

            assertEquals(mapOf(1 to 2, 2 to 1), counts)
        }
}
