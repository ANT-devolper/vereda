package dev.vereda.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.LocalTime

/** Round-trips reminders through a real Preferences DataStore backed by a temp file (pure JVM). */
class ReminderRepositoryTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val store: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(produceFile = { tmp.newFile("reminders.preferences_pb") })
    }
    private val repository: ReminderRepository by lazy { DefaultReminderRepository(store) }

    @Test
    fun `has no reminders by default`() =
        runTest {
            assertEquals(emptyList<LocalTime>(), repository.reminders())
        }

    @Test
    fun `stores and returns reminders sorted by time`() =
        runTest {
            repository.setReminders(listOf(LocalTime.of(20, 0), LocalTime.of(8, 30)))

            assertEquals(listOf(LocalTime.of(8, 30), LocalTime.of(20, 0)), repository.reminders())
        }

    @Test
    fun `deduplicates repeated times`() =
        runTest {
            repository.setReminders(listOf(LocalTime.of(8, 0), LocalTime.of(8, 0), LocalTime.of(9, 0)))

            assertEquals(listOf(LocalTime.of(8, 0), LocalTime.of(9, 0)), repository.reminders())
        }

    @Test
    fun `caps the number of reminders at three, keeping the earliest`() =
        runTest {
            repository.setReminders(
                listOf(LocalTime.of(18, 0), LocalTime.of(6, 0), LocalTime.of(12, 0), LocalTime.of(9, 0)),
            )

            assertEquals(
                listOf(LocalTime.of(6, 0), LocalTime.of(9, 0), LocalTime.of(12, 0)),
                repository.reminders(),
            )
        }
}
