package dev.vereda.appicon

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

/** Verifies the AlarmManager-backed icon scheduler via Robolectric's ShadowAlarmManager. */
@RunWith(AndroidJUnit4::class)
class AlarmAppIconSchedulerTest {
    private val zone: ZoneId = ZoneId.systemDefault()
    private val now = LocalDateTime.of(2026, 6, 30, 15, 0)
    private val clock: Clock = Clock.fixed(now.atZone(zone).toInstant(), zone)

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val shadowAlarmManager = shadowOf(alarmManager)
    private val scheduler = AlarmAppIconScheduler(context, clock)

    @Test
    fun `schedules a single alarm at the next boundary`() {
        scheduler.scheduleNext()

        val expected =
            LocalDateTime
                .of(2026, 6, 30, 18, 0)
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
        assertEquals(1, shadowAlarmManager.scheduledAlarms.size)
        assertEquals(expected, shadowAlarmManager.peekNextScheduledAlarm()!!.triggerAtTime)
    }
}
