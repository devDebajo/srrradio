package ru.debajo.srrradio.ui.host.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.common.outlinedTextFieldColors
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationEvent
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationNews
import ru.debajo.srrradio.ui.navigation.NavTree

@Composable
fun AddCustomStationScreen() {
    val viewModel: AddCustomStationViewModel = viewModel { AppApiHolder.get().addCustomStationViewModel }
    val state by viewModel.state.collectAsState()
    val navTree = NavTree.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(viewModel, navTree) {
        launch {
            delay(500)
            focusRequester.requestFocus()
        }
        viewModel.news.collect {
            when (it) {
                AddCustomStationNews.Close -> navTree.rootController.popBackStack()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.add_stream_title),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(12.dp),
            label = { Text(stringResource(R.string.stream_url)) },
            value = state.stream,
            colors = outlinedTextFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            onValueChange = { viewModel.onEvent(AddCustomStationEvent.OnStreamChanged(it)) },
            trailingIcon = {
                if (state.stream.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onEvent(AddCustomStationEvent.OnStreamChanged("")) }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.accessibility_clear_stream)
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text(stringResource(R.string.stream_name)) },
            value = state.name,
            colors = outlinedTextFieldColors(),
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            enabled = state.canEditName,
            onValueChange = { viewModel.onEvent(AddCustomStationEvent.OnNameChanged(it)) },
            trailingIcon = {
                if (state.searching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.dp,
                    )
                } else if (state.name.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onEvent(AddCustomStationEvent.OnNameChanged("")) }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.accessibility_clear_stream_name)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canSave,
            onClick = { viewModel.onEvent(AddCustomStationEvent.Save) },
        ) {
            Text(
                text = stringResource(R.string.save),
                fontSize = 16.sp,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
