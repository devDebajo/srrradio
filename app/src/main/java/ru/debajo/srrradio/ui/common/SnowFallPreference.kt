package ru.debajo.srrradio.ui.common

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.BooleanPreference

class SnowFallPreference(sharedPreferences: SharedPreferences) : BooleanPreference(sharedPreferences) {
    override val key: String = "enable_snaw_fall"

    override fun defaultValue(): Boolean = true
}
