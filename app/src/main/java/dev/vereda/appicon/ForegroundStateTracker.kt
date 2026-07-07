package dev.vereda.appicon

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * [AppForegroundState] backed by [Application.ActivityLifecycleCallbacks]: thin glue that forwards
 * activity start/stop to a [ForegroundCounter]. Register it in `Application.onCreate` so the app knows
 * when it is safe (in background) to switch the launcher icon aliases.
 */
class ForegroundStateTracker(
    onEnterBackground: () -> Unit,
) : Application.ActivityLifecycleCallbacks,
    AppForegroundState {
    private val counter = ForegroundCounter(onEnterBackground)

    override val isInForeground: Boolean get() = counter.isInForeground

    override fun onActivityStarted(activity: Activity) {
        counter.onStarted()
    }

    override fun onActivityStopped(activity: Activity) {
        counter.onStopped()
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?,
    ) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}
