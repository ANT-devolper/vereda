package dev.vereda.streak

import java.time.LocalDate
import java.util.SortedSet

/** Current and best reading streaks, in consecutive days. */
data class StreakResult(val current: Int, val best: Int)

/**
 * Computes reading streaks from the set of dates that have at least one completed chapter.
 *
 * The calculation is pure: callers pass the activity dates and the reference [today], so the result is
 * deterministic and easy to test. See [StreakCalculator.calculate] for the rules.
 */
class StreakCalculator {

    fun calculate(activityDates: Collection<LocalDate>, today: LocalDate): StreakResult {
        val days = activityDates.toSortedSet()
        if (days.isEmpty()) return StreakResult(current = 0, best = 0)

        return StreakResult(
            current = currentStreak(days, today),
            best = bestStreak(days),
        )
    }

    /**
     * Counts consecutive days ending today, or ending yesterday when today has no activity yet
     * (the streak is still alive). Returns 0 when both today and yesterday are missing.
     */
    private fun currentStreak(days: SortedSet<LocalDate>, today: LocalDate): Int {
        val anchor = when {
            today in days -> today
            today.minusDays(1) in days -> today.minusDays(1)
            else -> return 0
        }

        var streak = 0
        var day = anchor
        while (day in days) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }

    /** Longest run of consecutive days in the whole history. */
    private fun bestStreak(days: SortedSet<LocalDate>): Int {
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in days) {
            run = if (previous != null && day == previous.plusDays(1)) run + 1 else 1
            best = maxOf(best, run)
            previous = day
        }
        return best
    }
}
