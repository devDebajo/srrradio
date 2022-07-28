package ru.debajo.srrradio.ui.host.main.settings.logs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.ui.host.collection.ListScreen

@Composable
fun LogsListScreen() {
    val viewModel = viewModel { AppApiHolder.get().logsListViewModel }
    val helper = remember { AppApiHolder.get().sendErrorsHelper }
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.load()

        launch {
            viewModel.news.collect {
                when (it) {
                    is LogsScreenNews.OpenMailApp -> {
                        helper.openMailApp(context, it.path)
                    }
                }
            }
        }
    }
    val state by viewModel.state.collectAsState()

    ListScreen(
        title = stringResource(R.string.settings_send_logs),
        items = state,
        key = { it.title },
        contentType = { "log" }
    ) { logItem ->
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.onFileClick(logItem) },
            content = {
                Text(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    text = logItem.title
                )
            }
        )
    }
}
