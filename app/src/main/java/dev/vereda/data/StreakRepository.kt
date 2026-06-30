package dev.vereda.data

import dev.vereda.streak.StreakCalculator
import dev.vereda.streak.StreakResult
import java.time.Clock
import java.time.LocalDate

/**
 * Records daily reading activity and derives the reading streak from it.
 *
 * The [clock] is injectable so "today" is deterministic in tests; [streakCalculator] holds the streak rules.
 */
class StreakRepository(
    private val dao: DailyActivityDao,
    private val streakCalculator: StreakCalculator = StreakCalculator(),
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    /** Records that the user completed a chapter today, incrementing today's count. */
    suspend fun recordChapterCompleted() {
        val today = LocalDate.now(clock)
        val completedSoFar = dao.getByDate(today)?.chaptersCompleted ?: 0
        dao.upsert(DailyActivity(date = today, chaptersCompleted = completedSoFar + 1))
    }

    /** Current and best reading streaks, computed from all recorded activity dates. */
    suspend fun currentStreak(): StreakResult = streakCalculator.calculate(dao.getActivityDates(), LocalDate.now(clock))
}
