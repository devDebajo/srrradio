package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

abstract class NullableStringPreference(
    sharedPreferences: SharedPreferences
) : NullablePreference<String>(sharedPreferences) {

    override fun SharedPreferences.readNonNull(key: String): String = getStringOrThrow(key)

    override fun SharedPreferences.Editor.writeNonNull(key: String, value: String): SharedPreferences.Editor = putString(key, value)
}
