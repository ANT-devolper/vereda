package dev.vereda.reminders

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

/** Pure rule for the next date-time a daily reminder should fire. */
class ReminderSchedulingTest {
    private val now = LocalDateTime.of(2026, 6, 30, 10, 0)

    @Test
    fun `a time later today fires today`() {
        val next = ReminderScheduling.nextOccurrence(LocalTime.of(20, 0), now)

        assertEquals(LocalDateTime.of(2026, 6, 30, 20, 0), next)
    }

    @Test
    fun `a time earlier today fires tomorrow`() {
        val next = ReminderScheduling.nextOccurrence(LocalTime.of(8, 0), now)

        assertEquals(LocalDateTime.of(2026, 7, 1, 8, 0), next)
    }

    @Test
    fun `a time equal to now fires tomorrow`() {
        val next = ReminderScheduling.nextOccurrence(LocalTime.of(10, 0), now)

        assertEquals(LocalDateTime.of(2026, 7, 1, 10, 0), next)
    }
}
