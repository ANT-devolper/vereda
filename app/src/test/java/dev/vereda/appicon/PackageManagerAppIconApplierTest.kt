package dev.vereda.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Verifies the activity-alias based applier keeps exactly one launcher icon enabled. */
@RunWith(AndroidJUnit4::class)
class PackageManagerAppIconApplierTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageManager = context.packageManager
    private val applier = PackageManagerAppIconApplier(context)

    private fun state(alias: String): Int = packageManager.getComponentEnabledSetting(ComponentName(context.packageName, alias))

    @Test
    fun `applying red enables only the red alias`() {
        applier.apply(AppIcon.RED)

        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, state("dev.vereda.IconRed"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, state("dev.vereda.IconBlack"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, state("dev.vereda.IconYellow"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, state("dev.vereda.IconOrange"))
    }

    @Test
    fun `switching variants disables the previous one`() {
        applier.apply(AppIcon.YELLOW)
        applier.apply(AppIcon.BLACK)

        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, state("dev.vereda.IconBlack"))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, state("dev.vereda.IconYellow"))
    }
}
