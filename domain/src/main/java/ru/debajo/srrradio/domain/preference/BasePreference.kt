package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class BasePreference<T : Any?>(
    private val sharedPreferences: SharedPreferences,
) : ReadWriteProperty<Any, T> {

    val timestamp: Long
        get() = getPersistedTimestamp()

    abstract val key: String

    abstract fun defaultValue(): T

    abstract fun SharedPreferences.Editor.write(key: String, value: T): SharedPreferences.Editor

    abstract fun SharedPreferences.read(key: String): T

    fun get(): T {
        return if (key in sharedPreferences) {
            sharedPreferences.read(key)
        } else {
            defaultValue()
        }
    }

    fun set(value: T) {
        sharedPreferences.edit {
            if (value == null) {
                remove(key).remove(getTimestampKey())
            } else {
                write(key, value).persistNowEdited()
            }
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T = get()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = set(value)

    private fun SharedPreferences.Editor.persistNowEdited(): SharedPreferences.Editor {
        return putLong(getTimestampKey(), System.currentTimeMillis())
    }

    private fun getPersistedTimestamp(): Long {
        val key = getTimestampKey()
        return if (key in sharedPreferences) {
            sharedPreferences.getLong(key, 0L)
        } else {
            0L
        }
    }

    private fun getTimestampKey(): String = "${key}_timestamp"
}

fun SharedPreferences.getBooleanOrThrow(key: String): Boolean {
    if (key in this) {
        return getBoolean(key, false)
    }
    throwValueNotExist(key)
}

fun SharedPreferences.getStringOrThrow(key: String): String {
    if (key in this) {
        return getString(key, null)!!
    }
    throwValueNotExist(key)
}

private fun throwValueNotExist(key: String): Nothing {
    throw IllegalStateException("Value with key $key does not exist in preferences")
}
