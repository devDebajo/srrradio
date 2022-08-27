package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences

abstract class NullablePreference<T : Any>(
    sharedPreferences: SharedPreferences
) : BasePreference<T?>(sharedPreferences) {

    override fun defaultValue(): T? = null

    final override fun SharedPreferences.read(key: String): T = readNonNull(key)

    final override fun SharedPreferences.Editor.write(key: String, value: T?): SharedPreferences.Editor = writeNonNull(key, value!!)

    abstract fun SharedPreferences.readNonNull(key: String): T

    abstract fun SharedPreferences.Editor.writeNonNull(key: String, value: T): SharedPreferences.Editor
}
