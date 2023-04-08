package ru.debajo.srrradio.data.preference

import android.content.SharedPreferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.common.AppVersion
import ru.debajo.srrradio.domain.preference.BasePreference
import ru.debajo.srrradio.domain.preference.getStringOrThrow

class PreviousAppVersionPreference(
    sharedPreferences: SharedPreferences,
    private val json: Json,
) : BasePreference<AppVersion>(sharedPreferences) {

    override val key: String = "previous_app_version"

    override fun defaultValue(): AppVersion = Default

    override fun SharedPreferences.read(key: String): AppVersion {
        val rawJson = getStringOrThrow(key)
        return json.decodeFromString(SerializableAppVersion.serializer(), rawJson).convert()
    }

    override fun SharedPreferences.Editor.write(key: String, value: AppVersion): SharedPreferences.Editor {
        val stringValue = json.encodeToString(SerializableAppVersion.serializer(), value.convert())
        return putString(key, stringValue)
    }

    private fun SerializableAppVersion.convert(): AppVersion {
        return AppVersion(
            versionName = versionName,
            number = number,
        )
    }

    private fun AppVersion.convert(): SerializableAppVersion {
        return SerializableAppVersion(
            versionName = versionName,
            number = number,
        )
    }

    @Serializable
    private data class SerializableAppVersion(
        @SerialName("versionName")
        val versionName: String,

        @SerialName("number")
        val number: Int,
    )

    private companion object {
        val Default = AppVersion("1.0.8", 9)
    }
}
