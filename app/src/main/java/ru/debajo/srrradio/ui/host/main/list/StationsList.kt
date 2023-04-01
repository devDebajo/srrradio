package ru.debajo.srrradio.ui.host.main.list

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.filter
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.common.AppScreenTitle
import ru.debajo.srrradio.ui.common.AppTextButton
import ru.debajo.srrradio.ui.common.outlinedTextFieldColors
import ru.debajo.srrradio.ui.ext.Empty
import ru.debajo.srrradio.ui.ext.isNotEmpty
import ru.debajo.srrradio.ui.host.main.list.model.DefaultPlaylists
import ru.debajo.srrradio.ui.host.main.list.model.StationsListEvent
import ru.debajo.srrradio.ui.host.main.list.model.StationsListState
import ru.debajo.srrradio.ui.host.main.list.model.collectionEmpty
import ru.debajo.srrradio.ui.host.main.list.model.searchQuery
import ru.debajo.srrradio.ui.model.UiPlaylistIcon
import ru.debajo.srrradio.ui.model.UiPlaylistsElement
import ru.debajo.srrradio.ui.model.UiStationElement
import ru.debajo.srrradio.ui.model.UiTextElement
import ru.debajo.srrradio.ui.navigation.NavTree

@Composable
fun StationsList(bottomPadding: Dp, onScroll: () -> Unit) {
    val viewModel = StationsListViewModel.Local.current
    val state: StationsListState by viewModel.state.collectAsState()

    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = rememberCollapsingToolbarScaffoldState(),
        toolbar = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppScreenTitle(
                        text = stringResource(R.string.radio_title),
                    )

                    if (!state.collectionEmpty) {
                        Spacer(modifier = Modifier.weight(1f))

                        val navTree = NavTree.current
                        AppTextButton(
                            onClick = { navTree.collection.navigate() },
                            text = stringResource(R.string.track_collection),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search)) },
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(StationsListEvent.OnSearchQueryChanged(it)) },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 1,
                    singleLine = true,
                    colors = outlinedTextFieldColors(),
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onEvent(StationsListEvent.OnSearchQueryChanged(TextFieldValue.Empty)) }) {
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
                contentPadding = PaddingValues(bottom = bottomPadding)
            )
        },
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed
    )
}

@Composable
private fun ListContent(
    state: StationsListState,
    onScroll: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel = StationsListViewModel.Local.current
    val listState = rememberLazyListState()
    val navTree = NavTree.current
    LaunchedEffect(listState, onScroll) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { it }
            .collect { onScroll() }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
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
                                .animateItemPlacement()
                                .padding(horizontal = 16.dp),
                            station = element.station,
                            favorite = element.favorite,
                            playingState = element.playingState,
                            onFavoriteClick = { station, favorite -> viewModel.onEvent(StationsListEvent.ChangeFavorite(station, favorite)) },
                            onPlayClick = { station, playingState -> viewModel.onEvent(StationsListEvent.OnPlayPauseStation(station, playingState)) },
                            onClick = { station, playingState -> viewModel.onEvent(StationsListEvent.OnPlayPauseStation(station, playingState)) },
                        )
                    }

                    is UiTextElement -> TextElement(
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(horizontal = 16.dp),
                        element = element
                    )

                    is UiPlaylistsElement -> {
                        Playlists(
                            modifier = Modifier.animateItemPlacement(),
                            items = element.list,
                        ) {
                            navigateToPlaylist(navTree, it)
                        }
                    }
                }
            }
        )
    }
}

private fun navigateToPlaylist(navTree: NavTree, playlist: UiPlaylistIcon) {
    when (playlist) {
        DefaultPlaylists.NewStations -> {
            navTree.main.radio.newStations.navigate()
        }

        DefaultPlaylists.PopularStations -> {
            navTree.main.radio.popularStations.navigate()
        }

        DefaultPlaylists.FavoriteStations -> {
            navTree.main.radio.favoriteStations.navigate()
        }

        DefaultPlaylists.NearStations -> {
            navTree.main.radio.nearStations.navigate()
        }
    }
}

@Composable
private fun Playlists(
    modifier: Modifier = Modifier,
    items: List<UiPlaylistIcon>,
    onClick: (UiPlaylistIcon) -> Unit
) {
    val state = rememberScrollState()
    Row(
        modifier = modifier
            .horizontalScroll(state)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        for (item in items) {
            if (item === DefaultPlaylists.NearStations) {
                NearPlaylistCard(item = item, onClick = onClick)
            } else {
                PlaylistCard(item = item, onClick = onClick)
            }
        }
    }
}

@Composable
private fun TextElement(modifier: Modifier = Modifier, element: UiTextElement) {
    Text(
        modifier = modifier.padding(vertical = 8.dp),
        text = element.text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}
