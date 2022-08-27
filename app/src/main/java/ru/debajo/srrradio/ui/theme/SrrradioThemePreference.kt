package ru.debajo.srrradio.ui.theme

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.NullableStringPreference

class SrrradioThemePreference(sharedPreferences: SharedPreferences) : NullableStringPreference(sharedPreferences) {
    override val key: String = "CURRENT_THEME"
}
