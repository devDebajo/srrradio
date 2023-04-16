package ru.debajo.srrradio.ui.host.main.equalizer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.RadioEqualizer

class EqualizerViewModel(
    private val mediaController: MediaController,
) : ViewModel() {

    private val equalizer: RadioEqualizer
        get() = mediaController.equalizer

    private val _state: MutableStateFlow<EqualizerState> = MutableStateFlow(EqualizerState())
    val state: StateFlow<EqualizerState> = _state.asStateFlow()

    fun load() {
        val range = equalizer.bandRange
        val bands = (0 until equalizer.bandCount).map { index ->
            EqualizerBand.create(
                value = equalizer.getBandLevel(index),
                frequencyHz = equalizer.getFrequencyHz(index),
            )
        }
        _state.value = _state.value.copy(
            enabled = equalizer.enabled,
            bandMaxValue = range.last,
            bandMinValue = range.first,
            bands = bands,
            presets = equalizer.getPresets().map { it.name },
            selectedPresetIndex = equalizer.getCurrentPreset(),
        )
    }

    fun updateBand(index: Int, value: Int) {
        val state = _state.value
        val bandsMutable = state.bands.toMutableList()
        val newBand = bandsMutable.removeAt(index).copy(value = value)
        bandsMutable.add(index, newBand)
        _state.value = state.copy(
            bands = bandsMutable,
            selectedPresetIndex = -1,
        )

        equalizer.setBandLevel(index, value)
    }

    fun updateEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(enabled = enabled)
        equalizer.enabled = enabled
        saveToPreferences()
    }

    fun selectPreset(presetIndex: Int) {
        _state.value = _state.value.copy(selectedPresetIndex = presetIndex)
        equalizer.setPreset(presetIndex)

        load()
        saveToPreferences()
    }

    fun onValueChangeFinished() {
        saveToPreferences()
    }

    private fun saveToPreferences() {
        mediaController.saveEqualizerState()
    }
}
