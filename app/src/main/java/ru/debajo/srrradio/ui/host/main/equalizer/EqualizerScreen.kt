package ru.debajo.srrradio.ui.host.main.equalizer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import ru.debajo.srrradio.di.diViewModel

@Composable
fun EqualizerScreen() {
    val viewModel: EqualizerViewModel = diViewModel()
    LaunchedEffect(viewModel) { viewModel.load() }

    val state by viewModel.state.collectAsState()
    val range = state.bandMinValue.toFloat()..state.bandMaxValue.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Switch(
            checked = state.enabled,
            onCheckedChange = {
                viewModel.updateEnabled(it)
            }
        )

        Box {
            val expanded = remember { mutableStateOf(false) }
            DropdownMenuItem(
                enabled = state.enabled,
                text = { Text(state.getPresetName()) },
                onClick = { expanded.value = true }
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                for (presetIndex in state.presets.indices) {
                    val preset = state.presets[presetIndex]
                    DropdownMenuItem(
                        text = { Text(preset) },
                        onClick = {
                            viewModel.selectPreset(presetIndex)
                            expanded.value = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .height(400.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            for (bandIndex in state.bands.indices) {
                val band = state.bands[bandIndex]
                Band(
                    band = band,
                    range = range,
                    enabled = state.enabled,
                    onValueChangeFinished = {
                        viewModel.onValueChangeFinished()
                    }
                ) {
                    viewModel.updateBand(bandIndex, it)
                }
            }
        }
    }
}

@Composable
private fun Band(
    modifier: Modifier = Modifier,
    band: EqualizerBand,
    enabled: Boolean,
    range: ClosedFloatingPointRange<Float>,
    onValueChangeFinished: () -> Unit,
    onChange: (Int) -> Unit,
) {
    VerticalSlider(
        modifier = modifier,
        value = band.value.toFloat(),
        enabled = enabled,
        valueRange = range,
        onValueChange = { onChange(it.roundToInt()) },
        onValueChangeFinished = onValueChangeFinished,
    )
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .graphicsLayer {
                rotationZ = 270f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            },
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
    )
}
