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

@Composable
@OptIn(ExperimentalComposeUiApi::class)
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

        Text(pluralStringResource(R.plurals.sleep_timer_minutes, state.value, state.value))
        Spacer(Modifier.height(30.dp))
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = state.value.toFloat(),
            onValueChange = { viewModel.onValueChange(it) },
            onValueChangeFinished = { viewModel.onValueChangeFinished() },
            valueRange = 0f..90f,
            steps = 8,
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
