package dev.vereda.reminders

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

/** The broadcast receiver posts a reminder notification when its alarm fires. */
@RunWith(AndroidJUnit4::class)
class ReminderReceiverTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `posts a notification when triggered`() {
        ReminderReceiver().onReceive(context, Intent(context, ReminderReceiver::class.java))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assertEquals(1, shadowOf(notificationManager).allNotifications.size)
    }
}
