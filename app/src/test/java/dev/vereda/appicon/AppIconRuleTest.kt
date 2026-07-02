package dev.vereda.appicon

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

/** Pure rule mapping time + today's reading to the launcher icon and the next re-evaluation instant. */
class AppIconRuleTest {
    private fun at(
        hour: Int,
        minute: Int = 0,
    ) = LocalDateTime.of(2026, 6, 30, hour, minute)

    @Test
    fun `reading today keeps the icon black at any time`() {
        assertEquals(AppIcon.BLACK, AppIconRule.iconFor(at(21), readToday = true))
    }

    @Test
    fun `before noon the icon is black`() {
        assertEquals(AppIcon.BLACK, AppIconRule.iconFor(at(0), readToday = false))
        assertEquals(AppIcon.BLACK, AppIconRule.iconFor(at(11, 59), readToday = false))
    }

    @Test
    fun `from noon until 18h the icon is yellow`() {
        assertEquals(AppIcon.YELLOW, AppIconRule.iconFor(at(12), readToday = false))
        assertEquals(AppIcon.YELLOW, AppIconRule.iconFor(at(17, 59), readToday = false))
    }

    @Test
    fun `from 18h until 20h the icon is orange`() {
        assertEquals(AppIcon.ORANGE, AppIconRule.iconFor(at(18), readToday = false))
        assertEquals(AppIcon.ORANGE, AppIconRule.iconFor(at(19, 59), readToday = false))
    }

    @Test
    fun `from 20h until midnight the icon is red`() {
        assertEquals(AppIcon.RED, AppIconRule.iconFor(at(20), readToday = false))
        assertEquals(AppIcon.RED, AppIconRule.iconFor(at(23, 59), readToday = false))
    }

    @Test
    fun `next boundary in the morning is noon`() {
        assertEquals(at(12), AppIconRule.nextBoundary(at(9)))
    }

    @Test
    fun `next boundary in the afternoon is 18h`() {
        assertEquals(at(18), AppIconRule.nextBoundary(at(15)))
    }

    @Test
    fun `next boundary in the evening is 20h`() {
        assertEquals(at(20), AppIconRule.nextBoundary(at(19)))
    }

    @Test
    fun `next boundary at night is the following midnight`() {
        assertEquals(LocalDateTime.of(2026, 7, 1, 0, 0), AppIconRule.nextBoundary(at(22)))
    }

    @Test
    fun `at a boundary the next boundary is the following one`() {
        assertEquals(at(18), AppIconRule.nextBoundary(at(12)))
    }
}
