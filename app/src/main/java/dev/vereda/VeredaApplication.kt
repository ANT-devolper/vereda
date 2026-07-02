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
        // Re-evaluate the launcher icon color on every process start (day rolled over, boundary passed…).
        CoroutineScope(Dispatchers.Default).launch {
            container.appIconUpdater.refresh()
        }
    }
}
