package ru.debajo.srrradio.error

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.BooleanPreference

class SendingErrorsPreference(sharedPreferences: SharedPreferences) : BooleanPreference(sharedPreferences) {
    override val key: String = "SendingErrorsManager"
}
