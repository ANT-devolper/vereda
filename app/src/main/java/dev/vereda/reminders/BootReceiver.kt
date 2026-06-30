package dev.vereda.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.vereda.VeredaApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Re-schedules the user's reminders after a reboot, since alarms do not survive it. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val container = (context.applicationContext as VeredaApplication).container
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                container.reminderScheduler.schedule(container.reminderRepository.reminders())
            } finally {
                pendingResult.finish()
            }
        }
    }
}
