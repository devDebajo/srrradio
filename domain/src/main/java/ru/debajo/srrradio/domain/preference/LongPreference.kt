package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

abstract class LongPreference(
    sharedPreferences: SharedPreferences
) : BasePreference<Long>(sharedPreferences) {

    override fun defaultValue(): Long = 0L

    override fun SharedPreferences.Editor.write(key: String, value: Long): SharedPreferences.Editor = putLong(key, value)

    override fun SharedPreferences.read(key: String): Long = getLongOrThrow(key)
}
