package dev.vereda.reminders

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/** Verifies the AlarmManager-backed scheduler via Robolectric's ShadowAlarmManager. */
@RunWith(AndroidJUnit4::class)
class AlarmReminderSchedulerTest {
    private val zone: ZoneId = ZoneId.systemDefault()
    private val now = LocalDateTime.of(2026, 6, 30, 10, 0)
    private val clock: Clock = Clock.fixed(now.atZone(zone).toInstant(), zone)

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val shadowAlarmManager = shadowOf(alarmManager)
    private val scheduler = AlarmReminderScheduler(context, clock)

    @Test
    fun `schedules one alarm per reminder`() {
        scheduler.schedule(listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)))

        assertEquals(2, shadowAlarmManager.scheduledAlarms.size)
    }

    @Test
    fun `schedules the alarm at the next occurrence`() {
        scheduler.schedule(listOf(LocalTime.of(20, 0)))

        val expected =
            LocalDateTime
                .of(2026, 6, 30, 20, 0)
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
        assertEquals(expected, shadowAlarmManager.peekNextScheduledAlarm()!!.triggerAtTime)
    }

    @Test
    fun `cancelAll removes scheduled alarms`() {
        scheduler.schedule(listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)))

        scheduler.cancelAll()

        assertNull(shadowAlarmManager.peekNextScheduledAlarm())
    }

    @Test
    fun `rescheduling replaces the previous alarms`() {
        scheduler.schedule(listOf(LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(18, 0)))
        scheduler.schedule(listOf(LocalTime.of(8, 0)))

        assertEquals(1, shadowAlarmManager.scheduledAlarms.size)
    }
}
