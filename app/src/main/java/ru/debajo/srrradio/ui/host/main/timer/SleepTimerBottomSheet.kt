package ru.debajo.srrradio.ui.host.main.timer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.ext.stringResource
import ru.debajo.srrradio.ui.ext.textHandleMove

@Composable
fun SleepTimerBottomSheet() {
    val viewModel = SleepTimerViewModel.Local.current
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(stringResource(R.string.sleep_timer))
        Spacer(Modifier.height(30.dp))

        Text(formatSecondsMinutes(state.leftMinutes, state.leftSeconds))
        Spacer(Modifier.height(30.dp))

        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = state.leftMinutes.toFloat(),
            onValueChange = {
                haptic.textHandleMove()
                viewModel.onValueChange(it)
            },
            onValueChangeFinished = { viewModel.onValueChangeFinished() },
            valueRange = state.minValue..state.maxValue,
            steps = state.steps,
        )
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { viewModel.onButtonClick() }
        ) {
            Text(state.buttonIsSave.stringResource(R.string.sleep_timer_save, R.string.sleep_timer_cancel))
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun formatSecondsMinutes(leftMinutes: Int, leftSeconds: Int): String {
    val minutesString = pluralStringResource(R.plurals.sleep_timer_minutes, leftMinutes, leftMinutes)
    if (leftSeconds <= 0) {
        return minutesString
    }

    val secondsString = pluralStringResource(R.plurals.sleep_timer_seconds, leftSeconds, leftSeconds)
    return "$minutesString $secondsString"
}