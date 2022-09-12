package ru.debajo.srrradio.rate

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.IntPreference

class HostActivityCreateCountPreference(sharedPreferences: SharedPreferences) : IntPreference(sharedPreferences) {
    override val key: String = "host_activity_create_count"
}
