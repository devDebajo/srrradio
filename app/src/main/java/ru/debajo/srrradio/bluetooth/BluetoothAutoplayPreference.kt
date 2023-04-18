package ru.debajo.srrradio.bluetooth

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.preference.BooleanPreference

class BluetoothAutoplayPreference(sharedPreferences: SharedPreferences) : BooleanPreference(sharedPreferences) {
    override val key: String = "bluetooth_autoplay"
}
