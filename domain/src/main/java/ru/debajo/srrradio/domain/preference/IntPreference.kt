package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

abstract class IntPreference(
    sharedPreferences: SharedPreferences
) : BasePreference<Int>(sharedPreferences) {

    override fun defaultValue(): Int = 0

    override fun SharedPreferences.Editor.write(key: String, value: Int): SharedPreferences.Editor = putInt(key, value)

    override fun SharedPreferences.read(key: String): Int = getIntOrThrow(key)

    fun increment() {
        val currentValue = get()
        set(currentValue + 1)
    }
}
