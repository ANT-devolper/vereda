package dev.vereda.reminders

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/** Builds and posts the daily reading reminder notification. */
class ReminderNotifier(
    private val context: Context,
) {
    fun notifyReadingReminder() {
        val manager = NotificationManagerCompat.from(context)
        manager.createNotificationChannel(
            NotificationChannelCompat
                .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName("Lembretes de leitura")
                .setDescription("Lembra você de ler um capítulo por dia.")
                .build(),
        )

        val notification =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Hora da leitura")
                .setContentText("Leia um capítulo e mantenha sua sequência.")
                .setAutoCancel(true)
                .setContentIntent(openAppIntent())
                .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun openAppIntent(): PendingIntent? {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
        return PendingIntent.getActivity(
            context,
            0,
            launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val CHANNEL_ID = "reading_reminders"
        private const val NOTIFICATION_ID = 1
    }
}
