package ru.debajo.srrradio.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

class AppIconManager(
    private val context: Context,
) {
    fun isEnabled(icon: AppIcon): Boolean {
        val componentEnabledSetting = context.packageManager.getComponentEnabledSetting(icon.componentName)
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon == AppIcon.NEON
    }

    fun enable(icon: AppIcon) {
        AppIcon.values().forEach { updateIconState(icon = it, enabled = it == icon) }
    }

    private fun updateIconState(icon: AppIcon, enabled: Boolean) {
        if (isEnabled(icon) == enabled) {
            return
        }

        val stateFlag = if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        context.packageManager.setComponentEnabledSetting(
            icon.componentName,
            stateFlag,
            PackageManager.DONT_KILL_APP
        )
    }

    private val AppIcon.componentName: ComponentName
        get() = ComponentName(context.packageName, "ru.debajo.srrradio.ui.host.HostActivity.$componentSegment")
}