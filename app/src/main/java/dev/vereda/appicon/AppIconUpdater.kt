package dev.vereda.appicon

import dev.vereda.data.StreakRepository
import java.time.Clock
import java.time.LocalDateTime

/**
 * Re-evaluates the launcher icon from the current time and today's reading, applies it, and schedules
 * the next boundary check. Invoked on app start, on chapter completion, on each boundary alarm, and
 * after boot. The [clock] is injectable so "now" is deterministic in tests.
 */
class AppIconUpdater(
    private val streakRepository: StreakRepository,
    private val applier: AppIconApplier,
    private val scheduler: AppIconScheduler,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    suspend fun refresh() {
        val icon = AppIconRule.iconFor(LocalDateTime.now(clock), streakRepository.hasReadToday())
        applier.apply(icon)
        scheduler.scheduleNext()
    }
}
