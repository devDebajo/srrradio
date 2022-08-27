package ru.debajo.srrradio.icon

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.BooleanPreference

class DynamicIconPreference(sharedPreferences: SharedPreferences) : BooleanPreference(sharedPreferences) {

    override fun defaultValue(): Boolean = true

    override val key: String = "DYNAMIC_ICON"
}
