package dev.vereda.appicon

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Clock
import java.time.LocalDateTime

/** Schedules the next alarm at which the launcher icon should be re-evaluated. */
interface AppIconScheduler {
    fun scheduleNext()
}

/**
 * [AppIconScheduler] backed by [AlarmManager]. Sets a single inexact one-shot alarm at the next color
 * boundary (no exact-alarm permission needed); [IconUpdateReceiver] re-evaluates and re-schedules.
 */
class AlarmAppIconScheduler(
    private val context: Context,
    private val clock: Clock = Clock.systemDefaultZone(),
) : AppIconScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleNext() {
        val triggerAt =
            AppIconRule
                .nextBoundary(LocalDateTime.now(clock))
                .atZone(clock.zone)
                .toInstant()
                .toEpochMilli()
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent())
    }

    private fun pendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, IconUpdateReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private companion object {
        // Distinct from the reminder slot request codes (0..2).
        const val REQUEST_CODE = 100
    }
}
