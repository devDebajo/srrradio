package ru.debajo.srrradio.ui.host.main.equalizer

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.packInts
import androidx.compose.ui.util.unpackInt1
import androidx.compose.ui.util.unpackInt2

@Immutable
data class EqualizerState(
    val bandMaxValue: Int = 1500,
    val bandMinValue: Int = -1500,
    val bands: List<EqualizerBand> = emptyList(),
    val enabled: Boolean = false,
    val presets: List<String> = listOf(),
    val selectedPresetIndex: Int = 0,
) {

    fun getPresetName(index: Int = selectedPresetIndex): String {
        if (CustomPresetIndex == index) {
            return CustomPresetName
        }
        return presets.getOrNull(index).orEmpty()
    }

    companion object {
        const val CustomPresetName: String = "Custom"
        const val CustomPresetIndex: Int = -1
    }
}

@Immutable
@JvmInline
value class EqualizerBand(private val packed: Long) {
    val value: Int
        get() = unpackInt1(packed)

    val frequencyHz: Int
        get() = unpackInt2(packed)

    fun copy(
        value: Int = this.value,
        frequencyHz: Int = this.frequencyHz,
    ): EqualizerBand = create(value, frequencyHz)

    companion object {
        fun create(value: Int, frequencyHz: Int): EqualizerBand {
            return EqualizerBand(packInts(value, frequencyHz))
        }
    }
}
