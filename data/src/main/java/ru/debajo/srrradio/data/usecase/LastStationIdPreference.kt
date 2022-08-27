package ru.debajo.srrradio.data.usecase

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.NullableStringPreference

class LastStationIdPreference(sharedPreferences: SharedPreferences) : NullableStringPreference(sharedPreferences) {
    override val key: String = "LAST_STATION_ID"
}
