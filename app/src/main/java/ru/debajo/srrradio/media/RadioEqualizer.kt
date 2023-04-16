package ru.debajo.srrradio.media

import android.media.audiofx.Equalizer

class RadioEqualizer(private val equalizer: Equalizer) {

    var enabled: Boolean
        get() = equalizer.enabled
        set(value) {
            equalizer.enabled = value
        }

    val bandCount: Int
        get() = equalizer.numberOfBands.toInt()

    val bandRange: IntRange
        get() = equalizer.bandLevelRange[0].toInt()..equalizer.bandLevelRange[1].toInt()

    fun setBandLevel(index: Int, value: Int) {
        equalizer.setBandLevel(index.toShort(), value.coerceIn(bandRange).toShort())
    }

    fun getBandLevel(index: Int): Int {
        return equalizer.getBandLevel(index.toShort()).toInt()
    }

    fun getFrequencyHz(index: Int): Int {
        return (equalizer.getCenterFreq(index.toShort()) / 1000)
    }

    fun getPresets(): List<Preset> {
        return (0 until equalizer.numberOfPresets).map {
            Preset(
                code = it,
                name = equalizer.getPresetName(it.toShort())
            )
        }
    }

    fun setPreset(index: Int) {
        equalizer.usePreset(index.toShort())
    }

    fun getCurrentPreset(): Int = equalizer.currentPreset.toInt()

    fun dumpState(): State {
        return State(
            enabled = enabled,
            selectedPreset = getCurrentPreset(),
            bandsValues = (0 until bandCount).map { getBandLevel(it) }
        )
    }

    fun applyState(state: State) {
        enabled = state.enabled
        if (state.selectedPreset in 0 until equalizer.numberOfPresets.toInt()) {
            setPreset(state.selectedPreset)
        } else {
            for (bandIndex in state.bandsValues.indices) {
                setBandLevel(bandIndex, state.bandsValues[bandIndex])
            }
        }
    }

    class Preset(
        val code: Int,
        val name: String,
    )

    class State(
        val enabled: Boolean,
        val selectedPreset: Int,
        val bandsValues: List<Int>,
    )
}

