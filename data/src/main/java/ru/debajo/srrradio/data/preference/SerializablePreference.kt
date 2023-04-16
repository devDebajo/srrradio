package ru.debajo.srrradio.data.preference

import android.content.SharedPreferences
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.domain.preference.BasePreference
import ru.debajo.srrradio.domain.preference.getStringOrThrow

abstract class SerializablePreference<T>(
    sharedPreferences: SharedPreferences,
    private val json: Json,
) : BasePreference<T>(sharedPreferences) {

    abstract fun Json.serialize(input: T): String

    abstract fun Json.deserialize(input: String): T

    final override fun SharedPreferences.read(key: String): T {
        val rawJson = getStringOrThrow(key)
        return json.deserialize(rawJson)
    }

    final override fun SharedPreferences.Editor.write(key: String, value: T): SharedPreferences.Editor {
        val stringValue = json.serialize(value)
        return putString(key, stringValue)
    }
}
