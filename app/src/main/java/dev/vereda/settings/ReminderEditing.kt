package dev.vereda.settings

import java.time.LocalTime

/**
 * Pure rules for editing a reminder list while keeping its invariants: at most [MAX_REMINDERS]
 * entries, no duplicate times, always sorted. Shared by the onboarding and settings editors.
 */
object ReminderEditing {
    /** Adds [time] unless it would exceed [MAX_REMINDERS] or already exists. */
    fun add(
        current: List<LocalTime>,
        time: LocalTime,
    ): List<LocalTime> =
        if (current.size >= MAX_REMINDERS || time in current) {
            current
        } else {
            (current + time).sorted()
        }

    /** Replaces the reminder at [index] with [time], re-deduplicating and sorting. */
    fun update(
        current: List<LocalTime>,
        index: Int,
        time: LocalTime,
    ): List<LocalTime> {
        if (index !in current.indices) return current
        return current
            .toMutableList()
            .apply { this[index] = time }
            .distinct()
            .sorted()
    }

    /** Removes [time] from the list. */
    fun remove(
        current: List<LocalTime>,
        time: LocalTime,
    ): List<LocalTime> = current.filterNot { it == time }
}
