package ru.debajo.srrradio.domain.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import org.joda.time.DateTime

abstract class BasePreference<T : Any?>(
    private val sharedPreferences: SharedPreferences,
) : ReadWriteProperty<Any, T> {

    val timestamp: DateTime
        get() = getPersistedTimestamp()

    abstract val key: String

    abstract fun defaultValue(): T

    abstract fun SharedPreferences.Editor.write(key: String, value: T): SharedPreferences.Editor

    abstract fun SharedPreferences.read(key: String): T

    fun get(): T {
        return if (key in sharedPreferences) {
            runCatching { sharedPreferences.read(key) }.getOrElse { defaultValue() }
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
        val now = DateTime.now()
        return putString(getTimestampKey(), now.toString())
    }

    private fun getPersistedTimestamp(): DateTime {
        val key = getTimestampKey()
        return if (key in sharedPreferences) {
            sharedPreferences.getString(key, null)!!.let { DateTime.parse(it) }
        } else {
            DateTime(0)
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