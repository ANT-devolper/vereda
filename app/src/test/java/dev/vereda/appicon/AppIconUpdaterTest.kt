package dev.vereda.appicon

import dev.vereda.data.StreakRepository
import dev.vereda.streak.StreakResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
        foreground: Boolean = false,
    ) = AppIconUpdater(
        streakRepository = FakeStreakRepository(readToday),
        applier = applier,
        scheduler = scheduler,
        foregroundState = FakeAppForegroundState(foreground),
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

    // Regression: switching the alias in the foreground finishes the current task on Android 16 (the app
    // closes). refresh must not apply while in the foreground — it defers — but must still schedule.
    @Test
    fun `refresh does not apply while in the foreground but still schedules`() =
        runBlocking {
            updaterAt(LocalDateTime.of(2026, 6, 30, 19, 0), readToday = false, foreground = true).refresh()

            assertNull(applier.applied)
            assertEquals(1, scheduler.scheduleCount)
        }

    @Test
    fun `applyPending applies the icon deferred while in the foreground`() =
        runBlocking {
            val updater = updaterAt(LocalDateTime.of(2026, 6, 30, 19, 0), readToday = false, foreground = true)
            updater.refresh()
            assertNull(applier.applied)

            updater.applyPending()

            assertEquals(AppIcon.ORANGE, applier.applied)
        }

    @Test
    fun `applyPending does nothing when there is no deferred change`() =
        runBlocking {
            val updater = updaterAt(LocalDateTime.of(2026, 6, 30, 19, 0), readToday = false, foreground = false)
            updater.refresh()
            assertEquals(AppIcon.ORANGE, applier.applied)
            applier.applied = null

            updater.applyPending()

            assertNull(applier.applied)
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

private class FakeAppForegroundState(
    override val isInForeground: Boolean,
) : AppForegroundState

private class FakeStreakRepository(
    private val readToday: Boolean,
) : StreakRepository {
    override suspend fun recordChapterCompleted() = Unit

    override suspend fun currentStreak(): StreakResult = StreakResult(0, 0)

    override suspend fun hasReadToday(): Boolean = readToday
}
