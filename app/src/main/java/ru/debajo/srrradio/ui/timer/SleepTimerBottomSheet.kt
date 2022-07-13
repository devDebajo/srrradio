package ru.debajo.srrradio.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.ext.stringResource
import java.util.concurrent.TimeUnit

@Composable

fun SleepTimerBottomSheet() {
    val viewModel = SleepTimerViewModel.Local.current
    val state by viewModel.state.collectAsState()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(stringResource(R.string.sleep_timer))
        Spacer(Modifier.height(30.dp))

        Text(state.seconds.formatSecondsMinutes())
        Spacer(Modifier.height(30.dp))
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = state.minutes,
            onValueChange = { viewModel.onValueChange(it) },
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
private fun Long.formatSecondsMinutes(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this)
    val seconds = this - (minutes * 60L)
    val minutesString = pluralStringResource(R.plurals.sleep_timer_minutes, minutes.toInt(), minutes)
    if (seconds <= 0) {
        return minutesString
    }

    val secondsString = pluralStringResource(R.plurals.sleep_timer_seconds, seconds.toInt(), seconds)
    return "$minutesString $secondsString"
}