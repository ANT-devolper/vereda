package dev.vereda.appicon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verifies the pure foreground/background bookkeeping that gates when the launcher icon is switched. */
class ForegroundCounterTest {
    private var backgroundEvents = 0
    private val counter = ForegroundCounter(onEnterBackground = { backgroundEvents++ })

    @Test
    fun `starts in the background`() {
        assertFalse(counter.isInForeground)
    }

    @Test
    fun `is in the foreground while an activity is started`() {
        counter.onStarted()

        assertTrue(counter.isInForeground)
    }

    @Test
    fun `enters the background exactly once when the last activity stops`() {
        counter.onStarted()
        counter.onStopped()

        assertFalse(counter.isInForeground)
        assertEquals(1, backgroundEvents)
    }

    @Test
    fun `does not enter the background while another activity is still started`() {
        counter.onStarted()
        counter.onStarted()
        counter.onStopped()

        assertTrue(counter.isInForeground)
        assertEquals(0, backgroundEvents)

        counter.onStopped()

        assertFalse(counter.isInForeground)
        assertEquals(1, backgroundEvents)
    }

    @Test
    fun `does not fire when stopping with no started activity`() {
        counter.onStopped()

        assertFalse(counter.isInForeground)
        assertEquals(0, backgroundEvents)
    }
}
