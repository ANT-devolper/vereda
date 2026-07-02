package dev.vereda.appicon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.vereda.VeredaApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Re-evaluates the launcher icon when a scheduled boundary alarm fires. */
class IconUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val container = (context.applicationContext as VeredaApplication).container
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                container.appIconUpdater.refresh()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
