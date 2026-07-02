package dev.vereda.appicon

import java.time.LocalDateTime
import java.time.LocalTime

/** The launcher icon background variant, used to nudge the user as the day advances. */
enum class AppIcon {
    BLACK,
    YELLOW,
    ORANGE,
    RED,
}

/**
 * Pure rule mapping the current time (and whether the user already read today) to the launcher icon
 * variant, plus the next instant the icon should be re-evaluated.
 *
 * While the user has not read today the icon grows more urgent: black before noon, yellow until 18:00,
 * orange until 20:00, red afterwards. Reading today keeps it black. Boundaries are the round hours.
 */
object AppIconRule {
    private val NOON = LocalTime.of(12, 0)
    private val EVENING = LocalTime.of(18, 0)
    private val NIGHT = LocalTime.of(20, 0)

    /** The icon variant for [now], given whether a chapter was already completed today. */
    fun iconFor(
        now: LocalDateTime,
        readToday: Boolean,
    ): AppIcon {
        if (readToday) return AppIcon.BLACK
        val time = now.toLocalTime()
        return when {
            time < NOON -> AppIcon.BLACK
            time < EVENING -> AppIcon.YELLOW
            time < NIGHT -> AppIcon.ORANGE
            else -> AppIcon.RED
        }
    }

    /** The next date-time the icon color can change after [now]: the next of 12:00/18:00/20:00/midnight. */
    fun nextBoundary(now: LocalDateTime): LocalDateTime {
        val today = now.toLocalDate()
        val boundaries =
            listOf(
                today.atTime(NOON),
                today.atTime(EVENING),
                today.atTime(NIGHT),
                today.plusDays(1).atStartOfDay(),
            )
        return boundaries.first { it.isAfter(now) }
    }
}
