package dev.vereda.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/** Switches which launcher icon variant is currently active. */
interface AppIconApplier {
    fun apply(icon: AppIcon)
}

/**
 * [AppIconApplier] backed by manifest `activity-alias` components. Each [AppIcon] maps to one alias;
 * enabling the target first and disabling the rest keeps exactly one launcher entry active (no
 * duplicate or missing icon). [PackageManager.DONT_KILL_APP] avoids killing the app on the switch.
 */
class PackageManagerAppIconApplier(
    private val context: Context,
) : AppIconApplier {
    override fun apply(icon: AppIcon) {
        val packageManager = context.packageManager
        setEnabled(packageManager, icon, enabled = true)
        AppIcon.entries
            .filter { it != icon }
            .forEach { setEnabled(packageManager, it, enabled = false) }
    }

    private fun setEnabled(
        packageManager: PackageManager,
        icon: AppIcon,
        enabled: Boolean,
    ) {
        val state =
            if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
        packageManager.setComponentEnabledSetting(
            ComponentName(context.packageName, aliasFor(icon)),
            state,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun aliasFor(icon: AppIcon): String =
        when (icon) {
            AppIcon.BLACK -> "dev.vereda.IconBlack"
            AppIcon.YELLOW -> "dev.vereda.IconYellow"
            AppIcon.ORANGE -> "dev.vereda.IconOrange"
            AppIcon.RED -> "dev.vereda.IconRed"
        }
}
