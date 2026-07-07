package dev.vereda

import android.app.Application
import dev.vereda.di.AppContainer
import dev.vereda.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Holds the app-wide [AppContainer] so screens can resolve their dependencies. */
class VeredaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        // Track foreground/background so the launcher icon is only switched while in the background
        // (switching it in the foreground finishes the current task on Android 16 — the app "closes").
        registerActivityLifecycleCallbacks(container.foregroundTracker)
        // Re-evaluate the launcher icon color on every process start (day rolled over, boundary passed…).
        CoroutineScope(Dispatchers.Default).launch {
            container.appIconUpdater.refresh()
        }
    }
}
