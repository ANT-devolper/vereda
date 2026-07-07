package dev.vereda.appicon

import dev.vereda.data.StreakRepository
import java.time.Clock
import java.time.LocalDateTime

/**
 * Re-evaluates the launcher icon from the current time and today's reading, applies it, and schedules
 * the next boundary check. Invoked on app start, on chapter completion, on each boundary alarm, and
 * after boot. The [clock] is injectable so "now" is deterministic in tests.
 *
 * Switching a launcher `activity-alias` while the app is in the foreground finishes the current task on
 * Android 16 (the app "closes"), even with `DONT_KILL_APP`. So when the app is in the foreground the
 * change is deferred: the desired icon is remembered and applied by [applyPending] once the app goes to
 * the background — which is exactly when the launcher icon becomes visible anyway.
 */
class AppIconUpdater(
    private val streakRepository: StreakRepository,
    private val applier: AppIconApplier,
    private val scheduler: AppIconScheduler,
    private val foregroundState: AppForegroundState,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    private var pendingIcon: AppIcon? = null

    suspend fun refresh() {
        val icon = AppIconRule.iconFor(LocalDateTime.now(clock), streakRepository.hasReadToday())
        if (foregroundState.isInForeground) {
            pendingIcon = icon
        } else {
            applier.apply(icon)
            pendingIcon = null
        }
        scheduler.scheduleNext()
    }

    /** Applies any icon change deferred while the app was in the foreground. Call when it goes to background. */
    fun applyPending() {
        val icon = pendingIcon ?: return
        applier.apply(icon)
        pendingIcon = null
    }
}
