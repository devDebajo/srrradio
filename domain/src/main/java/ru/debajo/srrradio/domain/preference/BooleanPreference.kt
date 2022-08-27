package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

abstract class BooleanPreference(
    sharedPreferences: SharedPreferences
) : BasePreference<Boolean>(sharedPreferences) {

    override fun defaultValue(): Boolean = false

    override fun SharedPreferences.Editor.write(key: String, value: Boolean): SharedPreferences.Editor = putBoolean(key, value)

    override fun SharedPreferences.read(key: String): Boolean = getBooleanOrThrow(key)
}
