package ru.debajo.srrradio.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filter
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.ext.addPadding
import ru.debajo.srrradio.ui.list.model.StationsListEvent
import ru.debajo.srrradio.ui.list.model.StationsListState
import ru.debajo.srrradio.ui.model.UiStationElement
import ru.debajo.srrradio.ui.model.UiTextElement
import ru.debajo.srrradio.ui.player.PlayerBottomSheetPeekHeight
import ru.debajo.srrradio.ui.station.StationItem

@Composable
fun StationsList(onScroll: () -> Unit) {
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
                    text = stringResource(R.string.radio_title),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search)) },
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(StationsListEvent.OnSearchQueryChanged(it)) },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onEvent(StationsListEvent.OnSearchQueryChanged("")) }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = stringResource(R.string.accessibility_clear_search)
                                )
                            }
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
        body = {
            ListContent(
                state = state,
                onScroll = onScroll,
                contentPadding = PaddingValues(bottom = PlayerBottomSheetPeekHeight + 12.dp)
            )
        },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListContent(
    state: StationsListState,
    onScroll: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel = StationsListViewModel.Local.current
    val listState = rememberLazyListState()
    LaunchedEffect(listState, onScroll) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { it }
            .collect { onScroll() }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding.addPadding(horizontal = 16.dp),
    ) {
        items(
            count = state.uiElements.size,
            key = { state.uiElements[it].id },
            contentType = { state.uiElements[it].contentType },
            itemContent = { index ->
                when (val element = state.uiElements[index]) {
                    is UiStationElement -> {
                        StationItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            station = element.station,
                            playingState = element.playingState,
                            onPlayClick = { station, playingState -> viewModel.onEvent(StationsListEvent.OnPlayPauseStation(station, playingState)) }
                        )
                    }

                    is UiTextElement -> TextElement(
                        modifier = Modifier.animateItemPlacement(),
                        element = element
                    )
                }
            }
        )
    }
}

@Composable
private fun TextElement(modifier: Modifier = Modifier, element: UiTextElement) {
    Text(
        modifier = modifier.padding(vertical = 8.dp),
        text = element.text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary,
    )
}
