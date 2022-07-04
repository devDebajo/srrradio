package ru.debajo.srrradio.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import ru.debajo.srrradio.ui.ext.addPadding
import ru.debajo.srrradio.ui.list.reduktor.StationsListEvent
import ru.debajo.srrradio.ui.list.reduktor.StationsListState
import ru.debajo.srrradio.ui.player.PlayerBottomSheetPeekHeight
import ru.debajo.srrradio.ui.station.StationItem

@Composable
fun StationsList() {
    val viewModel = StationsListViewModel.Local.current
    val state by viewModel.state.collectAsState()

    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = rememberCollapsingToolbarScaffoldState(),
        toolbar = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Радио",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск") },
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(StationsListEvent.OnSearchQueryChanged(it)) },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
        },
        body = {
            ListContent(state, contentPadding = PaddingValues(bottom = PlayerBottomSheetPeekHeight + 12.dp))
        },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    )
}

@Composable
private fun ListContent(state: StationsListState, contentPadding: PaddingValues = PaddingValues(0.dp)) {
    val viewModel = StationsListViewModel.Local.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding.addPadding(horizontal = 16.dp),
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

val <T> T.exhaustive: T
    get() = this
