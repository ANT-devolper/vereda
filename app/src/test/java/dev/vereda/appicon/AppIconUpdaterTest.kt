package dev.vereda.appicon

import dev.vereda.data.StreakRepository
import dev.vereda.streak.StreakResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

/** Coordinates the icon rule, applier and scheduler; verified with fakes and a fixed clock. */
class AppIconUpdaterTest {
    private val applier = FakeAppIconApplier()
    private val scheduler = FakeAppIconScheduler()

    private fun updaterAt(
        now: LocalDateTime,
        readToday: Boolean,
    ) = AppIconUpdater(
        streakRepository = FakeStreakRepository(readToday),
        applier = applier,
        scheduler = scheduler,
        clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC),
    )

    @Test
    fun `refresh applies the color for the time when unread and schedules next`() =
        runBlocking {
            updaterAt(LocalDateTime.of(2026, 6, 30, 19, 0), readToday = false).refresh()

            assertEquals(AppIcon.ORANGE, applier.applied)
            assertEquals(1, scheduler.scheduleCount)
        }

    @Test
    fun `refresh keeps the icon black once the user has read today`() =
        runBlocking {
            updaterAt(LocalDateTime.of(2026, 6, 30, 22, 0), readToday = true).refresh()

            assertEquals(AppIcon.BLACK, applier.applied)
        }
}

private class FakeAppIconApplier : AppIconApplier {
    var applied: AppIcon? = null

    override fun apply(icon: AppIcon) {
        applied = icon
    }
}

private class FakeAppIconScheduler : AppIconScheduler {
    var scheduleCount = 0

    override fun scheduleNext() {
        scheduleCount++
    }
}

private class FakeStreakRepository(
    private val readToday: Boolean,
) : StreakRepository {
    override suspend fun recordChapterCompleted() = Unit

    override suspend fun currentStreak(): StreakResult = StreakResult(0, 0)

    override suspend fun hasReadToday(): Boolean = readToday
}
