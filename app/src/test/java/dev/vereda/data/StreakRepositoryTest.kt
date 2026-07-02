package dev.vereda.data

import dev.vereda.streak.StreakResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Unit tests for the repository that ties daily activity persistence to the streak calculation.
 * Uses a fake in-memory DAO (the real DAO is covered by [DailyActivityDaoTest]) and a fixed clock.
 */
class StreakRepositoryTest {
    private val today = LocalDate.of(2026, 6, 30)
    private val dao = FakeDailyActivityDao()
    private val repository = DefaultStreakRepository(dao = dao, clock = fixedClockAt(today))

    @Test
    fun `recording a completion creates today's row with count one`() =
        runBlocking {
            repository.recordChapterCompleted()

            assertEquals(1, dao.getByDate(today)?.chaptersCompleted)
        }

    @Test
    fun `recording again the same day increments the count`() =
        runBlocking {
            repository.recordChapterCompleted()
            repository.recordChapterCompleted()
            repository.recordChapterCompleted()

            assertEquals(3, dao.getByDate(today)?.chaptersCompleted)
        }

    @Test
    fun `streak is zero when there is no activity`() =
        runBlocking {
            assertEquals(StreakResult(current = 0, best = 0), repository.currentStreak())
        }

    @Test
    fun `recording today starts a current streak of one`() =
        runBlocking {
            repository.recordChapterCompleted()

            assertEquals(StreakResult(current = 1, best = 1), repository.currentStreak())
        }

    @Test
    fun `hasReadToday is false without any activity today`() =
        runBlocking {
            assertEquals(false, repository.hasReadToday())
        }

    @Test
    fun `hasReadToday is true after recording a completion today`() =
        runBlocking {
            repository.recordChapterCompleted()

            assertEquals(true, repository.hasReadToday())
        }

    @Test
    fun `hasReadToday is false when only past days have activity`() =
        runBlocking {
            dao.upsert(DailyActivity(today.minusDays(1), chaptersCompleted = 1))

            assertEquals(false, repository.hasReadToday())
        }

    @Test
    fun `streak counts consecutive days up to today`() =
        runBlocking {
            dao.upsert(DailyActivity(today.minusDays(1), chaptersCompleted = 1))
            dao.upsert(DailyActivity(today.minusDays(2), chaptersCompleted = 1))
            repository.recordChapterCompleted()

            assertEquals(StreakResult(current = 3, best = 3), repository.currentStreak())
        }

    private fun fixedClockAt(date: LocalDate): Clock = Clock.fixed(date.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC)
}

/** In-memory [DailyActivityDao] for fast repository unit tests. */
private class FakeDailyActivityDao : DailyActivityDao {
    private val rows = mutableMapOf<LocalDate, DailyActivity>()

    override suspend fun upsert(activity: DailyActivity) {
        rows[activity.date] = activity
    }

    override suspend fun getByDate(date: LocalDate): DailyActivity? = rows[date]

    override suspend fun getActivityDates(): List<LocalDate> = rows.keys.sorted()
}
