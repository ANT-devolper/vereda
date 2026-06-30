package dev.vereda.reminders

import java.time.LocalDateTime
import java.time.LocalTime

/** Pure scheduling rule shared by the scheduler and its tests. */
object ReminderScheduling {
    /** The next date-time [time] occurs at or after [now]: today if still ahead, otherwise tomorrow. */
    fun nextOccurrence(
        time: LocalTime,
        now: LocalDateTime,
    ): LocalDateTime {
        val todayAt = now.toLocalDate().atTime(time)
        return if (todayAt.isAfter(now)) todayAt else todayAt.plusDays(1)
    }
}
