package dev.vereda.streak

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Streak rules (MVP):
 * - A day counts when it has at least one completed chapter (a date is present in the activity set).
 * - The current streak is the run of consecutive days ending today, or ending yesterday when today has
 *   no activity yet (the streak is still alive until the day ends).
 * - Missing a full day (no activity today and none yesterday) resets the current streak to zero.
 * - The best streak is the longest run of consecutive days ever recorded.
 */
class StreakCalculatorTest {
    private val calculator = StreakCalculator()
    private val today = LocalDate.of(2026, 6, 29)

    @Test
    fun `no activity yields zero streaks`() {
        val result = calculator.calculate(activityDates = emptySet(), today = today)

        assertEquals(0, result.current)
        assertEquals(0, result.best)
    }

    @Test
    fun `reading today gives a current streak of one`() {
        val result = calculator.calculate(activityDates = setOf(today), today = today)

        assertEquals(1, result.current)
        assertEquals(1, result.best)
    }

    @Test
    fun `consecutive days ending today accumulate`() {
        val dates = setOf(today, today.minusDays(1), today.minusDays(2))

        val result = calculator.calculate(activityDates = dates, today = today)

        assertEquals(3, result.current)
        assertEquals(3, result.best)
    }

    @Test
    fun `streak stays alive when today has no activity but yesterday does`() {
        val dates = setOf(today.minusDays(1), today.minusDays(2))

        val result = calculator.calculate(activityDates = dates, today = today)

        assertEquals(2, result.current)
        assertEquals(2, result.best)
    }

    @Test
    fun `missing both today and yesterday resets the current streak`() {
        val dates = setOf(today.minusDays(2), today.minusDays(3))

        val result = calculator.calculate(activityDates = dates, today = today)

        assertEquals(0, result.current)
        assertEquals(2, result.best)
    }

    @Test
    fun `a gap breaks the run and best streak reflects the longest past run`() {
        val dates =
            setOf(
                // current run: yesterday + today = 2
                today,
                today.minusDays(1),
                // older run of 4 consecutive days
                today.minusDays(5),
                today.minusDays(6),
                today.minusDays(7),
                today.minusDays(8),
            )

        val result = calculator.calculate(activityDates = dates, today = today)

        assertEquals(2, result.current)
        assertEquals(4, result.best)
    }

    @Test
    fun `duplicate and unordered dates are handled`() {
        val dates =
            listOf(
                today.minusDays(2),
                today,
                today,
                today.minusDays(1),
                today.minusDays(1),
            )

        val result = calculator.calculate(activityDates = dates, today = today)

        assertEquals(3, result.current)
        assertEquals(3, result.best)
    }
}
