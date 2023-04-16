package ru.debajo.srrradio.media

import android.content.SharedPreferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.data.preference.SerializablePreference

class RadioEqualizerPreference(
    sharedPreferences: SharedPreferences,
    json: Json,
) : SerializablePreference<RadioEqualizer.State>(sharedPreferences, json) {

    override val key: String = "equalizer_state"

    override fun Json.serialize(input: RadioEqualizer.State): String {
        return encodeToString(SerializableState.serializer(), input.convert())
    }

    override fun Json.deserialize(input: String): RadioEqualizer.State {
        return decodeFromString(SerializableState.serializer(), input).convert()
    }

    private fun RadioEqualizer.State.convert(): SerializableState {
        return SerializableState(
            enabled = enabled,
            selectedPreset = selectedPreset,
            bandsValues = bandsValues,
        )
    }

    private fun SerializableState.convert(): RadioEqualizer.State {
        return RadioEqualizer.State(
            enabled = enabled,
            selectedPreset = selectedPreset,
            bandsValues = bandsValues,
        )
    }

    @Serializable
    private data class SerializableState(
        @SerialName("enabled")
        val enabled: Boolean,

        @SerialName("selectedPreset")
        val selectedPreset: Int,

        @SerialName("bandsValues")
        val bandsValues: List<Int>,
    )

    override fun defaultValue(): RadioEqualizer.State {
        return RadioEqualizer.State(
            enabled = false,
            selectedPreset = 0,
            bandsValues = emptyList()
        )
    }
}
