package ru.debajo.srrradio.icon

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager

class AppIconManager(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
) {

    var dynamicIcon: Boolean
        get() = sharedPreferences.getBoolean(DYNAMIC_ICON, false)
        set(value) {
            sharedPreferences.edit()
                .putBoolean(DYNAMIC_ICON, value)
                .apply()
        }

    fun isEnabled(icon: AppIcon): Boolean {
        val componentEnabledSetting = context.packageManager.getComponentEnabledSetting(icon.componentName)
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon == AppIcon.DEFAULT
    }

    fun enable(icon: AppIcon) {
        if (dynamicIcon) {
            AppIcon.values().forEach { updateIconState(icon = it, enabled = it == icon) }
        } else {
            AppIcon.values().forEach { updateIconState(icon = it, enabled = it == AppIcon.DEFAULT) }
        }
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

    private companion object {
        const val DYNAMIC_ICON = "DYNAMIC_ICON"
    }
}


