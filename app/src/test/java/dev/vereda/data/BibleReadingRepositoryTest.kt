package dev.vereda.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.vereda.progress.PortugueseBibleCatalog
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Loads chapters for the reading screen from the bundled Bible database (via Room.createFromAsset)
 * combined with the book catalog. Runs on the JVM via Robolectric.
 */
@RunWith(AndroidJUnit4::class)
class BibleReadingRepositoryTest {
    private lateinit var database: BibleContentDatabase
    private lateinit var repository: BibleReadingRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .databaseBuilder(context, BibleContentDatabase::class.java, "bible-reading-test.db")
                .createFromAsset("bible.db")
                .build()
        repository = DefaultBibleReadingRepository(verseDao = database.verseDao(), catalog = PortugueseBibleCatalog())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `loads a chapter with its book name and verses in order`() =
        runBlocking {
            val chapter = repository.chapter(bookId = 1, chapter = 1)

            assertEquals("Gênesis", chapter.bookName)
            assertEquals(1, chapter.chapter)
            assertEquals(31, chapter.verses.size)
            assertEquals(1, chapter.verses.first().number)
            assertEquals(31, chapter.verses.last().number)
            assertEquals("No princípio criou Deus os céus e a terra.", chapter.verses.first().text)
        }
}
