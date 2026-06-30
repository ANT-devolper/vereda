package dev.vereda.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies that the bundled Bible text loads from assets/bible.db via Room.createFromAsset and that
 * chapters can be read back. Runs on the JVM via Robolectric.
 */
@RunWith(AndroidJUnit4::class)
class BibleContentDatabaseTest {
    private lateinit var database: BibleContentDatabase
    private lateinit var dao: VerseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .databaseBuilder(context, BibleContentDatabase::class.java, "bible-test.db")
                .createFromAsset("bible.db")
                .build()
        dao = database.verseDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `reads Genesis 1 with its 31 verses in order`() =
        runBlocking {
            val verses = dao.getChapter(bookId = 1, chapter = 1)

            assertEquals(31, verses.size)
            assertEquals(1, verses.first().verse)
            assertEquals(31, verses.last().verse)
            assertEquals("No princípio criou Deus os céus e a terra.", verses.first().text)
        }

    @Test
    fun `reads a known verse from the New Testament`() =
        runBlocking {
            val john = dao.getChapter(bookId = 43, chapter = 3)
            val verse16 = john.single { it.verse == 16 }

            assertTrue(verse16.text.startsWith("Porque Deus amou ao mundo"))
        }
}
