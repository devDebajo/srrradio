package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

abstract class FloatPreference(
    sharedPreferences: SharedPreferences
) : BasePreference<Float>(sharedPreferences) {

    override fun defaultValue(): Float = 0f

    override fun SharedPreferences.Editor.write(key: String, value: Float): SharedPreferences.Editor = putFloat(key, value)

    override fun SharedPreferences.read(key: String): Float = getFloatOrThrow(key)
}
