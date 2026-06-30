package dev.vereda

import android.app.Application
import dev.vereda.di.AppContainer
import dev.vereda.di.DefaultAppContainer

/** Holds the app-wide [AppContainer] so screens can resolve their dependencies. */
class VeredaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
