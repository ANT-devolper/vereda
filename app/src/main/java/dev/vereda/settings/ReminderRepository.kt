package dev.vereda.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** The most reminders a user may configure per day. */
const val MAX_REMINDERS = 3

/** Persists the user's daily reminder times (up to [MAX_REMINDERS]). */
interface ReminderRepository {
    /** The configured reminders, deduplicated and sorted by time. */
    suspend fun reminders(): List<LocalTime>

    /** Replaces the reminders, deduplicating, sorting and capping at [MAX_REMINDERS]. */
    suspend fun setReminders(times: List<LocalTime>)
}

/** DataStore-backed [ReminderRepository]; times are stored as `"HH:mm"` strings. */
class DefaultReminderRepository(
    private val dataStore: DataStore<Preferences>,
) : ReminderRepository {
    override suspend fun reminders(): List<LocalTime> {
        val stored = dataStore.data.first()[KEY].orEmpty()
        return stored.map { LocalTime.parse(it, FORMAT) }.normalize()
    }

    override suspend fun setReminders(times: List<LocalTime>) {
        val normalized = times.normalize()
        dataStore.edit { preferences ->
            preferences[KEY] = normalized.map { it.format(FORMAT) }.toSet()
        }
    }

    private fun List<LocalTime>.normalize(): List<LocalTime> = distinct().sorted().take(MAX_REMINDERS)

    private companion object {
        val KEY = stringSetPreferencesKey("reminders")
        val FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
