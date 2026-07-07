package dev.vereda.appicon

/** Whether the app currently has at least one activity in the foreground (started). */
interface AppForegroundState {
    val isInForeground: Boolean
}
