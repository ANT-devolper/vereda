package dev.vereda.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Posts the reading reminder notification when a scheduled alarm fires. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        ReminderNotifier(context).notifyReadingReminder()
    }
}
