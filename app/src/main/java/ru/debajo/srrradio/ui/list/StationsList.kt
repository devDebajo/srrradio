package ru.debajo.srrradio.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.debajo.srrradio.ui.list.reduktor.StationsListEvent
import ru.debajo.srrradio.ui.list.reduktor.StationsListState
import ru.debajo.srrradio.ui.station.StationItem

@Composable
fun StationsList() {
    val viewModel = StationsListViewModel.Local.current

    val state by viewModel.state.collectAsState()
    when (val localState = state.exhaustive) {
        is StationsListState.Empty -> Text("Пусто")
        is StationsListState.Loading -> Text("Загрузка")
        is StationsListState.Data -> ListContent(localState)
    }
}

@Composable
private fun ListContent(state: StationsListState.Data) {
    val viewModel = StationsListViewModel.Local.current
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                viewModel.onEvent(StationsListEvent.OnSearchQueryChanged(it))
            }
        )

        LazyColumn {
            items(
                count = state.stations.size,
                key = { state.stations[it].id },
                contentType = { "UiStation" },
                itemContent = { StationItem(state.stations[it]) }
            )
        }
    }
}

val <T> T.exhaustive: T
    get() = this
