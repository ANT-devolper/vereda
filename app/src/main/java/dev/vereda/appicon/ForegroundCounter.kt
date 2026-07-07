package dev.vereda.appicon

/**
 * Pure counter of started activities, used to know whether the app is in the foreground. Reaching zero
 * started activities means the app went to the background, at which point [onEnterBackground] fires once
 * — this is the safe moment to switch the launcher icon aliases (see [PackageManagerAppIconApplier]).
 */
class ForegroundCounter(
    private val onEnterBackground: () -> Unit,
) {
    private var started = 0

    val isInForeground: Boolean get() = started > 0

    fun onStarted() {
        started++
    }

    fun onStopped() {
        if (started > 0 && --started == 0) {
            onEnterBackground()
        }
    }
}
