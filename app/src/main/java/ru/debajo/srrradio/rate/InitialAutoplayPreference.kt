package ru.debajo.srrradio.rate

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.BooleanPreference

class InitialAutoplayPreference(sharedPreferences: SharedPreferences) : BooleanPreference(sharedPreferences) {
    override val key: String = "initial_autoplay"
}
