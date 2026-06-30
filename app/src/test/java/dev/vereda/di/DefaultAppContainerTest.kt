package dev.vereda.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies the production dependency container wires real Room-backed repositories
 * that actually persist data. Uses Robolectric so the Room database is created on the JVM.
 */
@RunWith(AndroidJUnit4::class)
class DefaultAppContainerTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `progress repository persists read chapters`() =
        runBlocking {
            val container = DefaultAppContainer(context)

            container.progressRepository.markChapterRead(bookId = 1, chapter = 1)

            assertEquals(1, container.progressRepository.overallProgress().chaptersRead)
        }

    @Test
    fun `streak repository records daily activity`() =
        runBlocking {
            val container = DefaultAppContainer(context)

            container.streakRepository.recordChapterCompleted()

            assertEquals(1, container.streakRepository.currentStreak().current)
        }
}
