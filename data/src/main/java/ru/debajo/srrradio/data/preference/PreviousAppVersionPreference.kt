package ru.debajo.srrradio.data.preference

import android.content.SharedPreferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.common.AppVersion

class PreviousAppVersionPreference(
    sharedPreferences: SharedPreferences,
    json: Json,
) : SerializablePreference<AppVersion>(sharedPreferences, json) {

    override val key: String = "previous_app_version"

    override fun defaultValue(): AppVersion = Default

    override fun Json.serialize(input: AppVersion): String {
        return encodeToString(SerializableAppVersion.serializer(), input.convert())
    }

    override fun Json.deserialize(input: String): AppVersion {
        return decodeFromString(SerializableAppVersion.serializer(), input).convert()
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
