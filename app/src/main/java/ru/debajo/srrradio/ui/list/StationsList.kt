package ru.debajo.srrradio.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Поиск") },
            value = state.searchQuery,
            onValueChange = { viewModel.onEvent(StationsListEvent.OnSearchQueryChanged(it)) },
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(
                count = state.stations.size,
                key = { state.stations[it].id },
                contentType = { "UiStation" },
                itemContent = { index ->
                    StationItem(
                        modifier = Modifier.fillMaxWidth(),
                        station = state.stations[index],
                        playingState = state.stationPlayingState(index),
                        onPlayClick = { station, playingState -> viewModel.onEvent(StationsListEvent.OnPlayPauseStation(station, playingState)) }
                    )
                }
            )
        }
    }
}

val <T> T.exhaustive: T
    get() = this
