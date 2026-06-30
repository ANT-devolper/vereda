package dev.vereda.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.vereda.settings.MAX_REMINDERS
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalTime

/** Schedules (and cancels) the user's daily reminder alarms. */
interface ReminderScheduler {
    /** Replaces all scheduled reminders with daily alarms for [times]. */
    fun schedule(times: List<LocalTime>)

    /** Cancels every scheduled reminder. */
    fun cancelAll()
}

/**
 * [ReminderScheduler] backed by [AlarmManager]. Uses inexact daily repeating alarms (no exact-alarm
 * permission needed); each reminder slot gets a stable request code so it can be cancelled and replaced.
 */
class AlarmReminderScheduler(
    private val context: Context,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ReminderScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(times: List<LocalTime>) {
        cancelAll()
        val now = LocalDateTime.now(clock)
        times.take(MAX_REMINDERS).forEachIndexed { index, time ->
            val triggerAt =
                ReminderScheduling
                    .nextOccurrence(time, now)
                    .atZone(clock.zone)
                    .toInstant()
                    .toEpochMilli()
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                AlarmManager.INTERVAL_DAY,
                pendingIntent(index),
            )
        }
    }

    override fun cancelAll() {
        for (index in 0 until MAX_REMINDERS) {
            alarmManager.cancel(pendingIntent(index))
        }
    }

    private fun pendingIntent(index: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            index,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
