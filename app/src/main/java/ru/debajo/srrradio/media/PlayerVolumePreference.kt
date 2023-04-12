package ru.debajo.srrradio.media

import android.content.SharedPreferences
import kotlin.reflect.KProperty
import ru.debajo.srrradio.domain.preference.FloatPreference

class PlayerVolumePreference(sharedPreferences: SharedPreferences) : FloatPreference(sharedPreferences) {
    override val key: String = "player_volume"

    override fun getValue(thisRef: Any, property: KProperty<*>): Float {
        return super.getValue(thisRef, property).coerceIn(0f, 1f)
    }

    override fun defaultValue(): Float = 1f
}
