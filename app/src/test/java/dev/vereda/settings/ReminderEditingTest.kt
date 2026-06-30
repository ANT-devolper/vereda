package dev.vereda.settings

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

/** Pure list-editing rules shared by the onboarding and settings reminder editors. */
class ReminderEditingTest {
    private val eight = LocalTime.of(8, 0)
    private val noon = LocalTime.of(12, 0)
    private val twenty = LocalTime.of(20, 0)

    @Test
    fun `add keeps the list sorted`() {
        assertEquals(listOf(eight, twenty), ReminderEditing.add(listOf(twenty), eight))
    }

    @Test
    fun `add ignores duplicates`() {
        assertEquals(listOf(eight), ReminderEditing.add(listOf(eight), eight))
    }

    @Test
    fun `add ignores a fourth reminder`() {
        val full = listOf(eight, noon, twenty)
        assertEquals(full, ReminderEditing.add(full, LocalTime.of(9, 0)))
    }

    @Test
    fun `update replaces a reminder and re-sorts`() {
        val updated = ReminderEditing.update(listOf(eight, twenty), index = 0, time = LocalTime.of(22, 0))
        assertEquals(listOf(twenty, LocalTime.of(22, 0)), updated)
    }

    @Test
    fun `remove drops the given time`() {
        assertEquals(listOf(eight), ReminderEditing.remove(listOf(eight, twenty), twenty))
    }
}
