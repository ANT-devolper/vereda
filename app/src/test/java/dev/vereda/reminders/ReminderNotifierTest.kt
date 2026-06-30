package dev.vereda.reminders

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

/** Verifies the reminder notification is posted on its own channel (Robolectric). */
@RunWith(AndroidJUnit4::class)
class ReminderNotifierTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val shadow = shadowOf(notificationManager)

    @Test
    fun `posts a reading reminder notification`() {
        ReminderNotifier(context).notifyReadingReminder()

        assertEquals(1, shadow.allNotifications.size)
    }

    @Test
    fun `creates the reminders notification channel`() {
        ReminderNotifier(context).notifyReadingReminder()

        assertNotNull(notificationManager.getNotificationChannel(ReminderNotifier.CHANNEL_ID))
    }
}
