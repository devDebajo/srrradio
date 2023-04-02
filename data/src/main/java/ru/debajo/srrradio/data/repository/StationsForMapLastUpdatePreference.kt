package ru.debajo.srrradio.data.repository

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.LongPreference

internal class StationsForMapLastUpdatePreference(sharedPreferences: SharedPreferences) : LongPreference(sharedPreferences) {
    override val key: String = "stations_for_map_last_update"
}
