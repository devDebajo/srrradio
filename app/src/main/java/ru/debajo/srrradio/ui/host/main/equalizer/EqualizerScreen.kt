package ru.debajo.srrradio.ui.host.main.equalizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.diViewModel
import ru.debajo.srrradio.ui.common.AppScreenTitle

@Composable
fun EqualizerScreen() {
    val viewModel: EqualizerViewModel = diViewModel()
    LaunchedEffect(viewModel) { viewModel.load() }

    val state by viewModel.state.collectAsState()
    val range = state.bandMinValue.toFloat()..state.bandMaxValue.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
    ) {
        AppScreenTitle(text = stringResource(id = R.string.equalizer))

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Включен:"
            )
            Switch(
                checked = state.enabled,
                onCheckedChange = {
                    viewModel.updateEnabled(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Пресет:"
            )

            Box {
                val expanded = remember { mutableStateOf(false) }
                Button(
                    enabled = state.enabled,
                    onClick = { expanded.value = true }
                ) {
                    Text(state.getPresetName())
                }
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
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier
                .height(350.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
    Box(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .verticalLayout()
                .align(Alignment.BottomStart),
            text = "${band.frequencyHz} Hz",
            fontSize = 9.sp
        )

        Slider(
            modifier = Modifier.verticalLayout(),
            value = band.value.toFloat(),
            enabled = enabled,
            valueRange = range,
            onValueChange = { onChange(it.roundToInt()) },
            onValueChangeFinished = onValueChangeFinished,
        )
    }
}

@Stable
private fun Modifier.verticalLayout(): Modifier {
    return graphicsLayer {
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
        }
}