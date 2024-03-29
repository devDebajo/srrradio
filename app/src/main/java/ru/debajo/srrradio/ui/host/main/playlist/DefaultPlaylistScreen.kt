package ru.debajo.srrradio.ui.host.main.playlist

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.diViewModel
import ru.debajo.srrradio.ui.host.collection.ListScreen
import ru.debajo.srrradio.ui.host.main.list.StationItem

enum class DefaultPlaylistScreenStrategy(
    val titleRes: Int,
    val emptyPlaceholderTextRes: Int
) {
    POPULAR(R.string.playlist_popular, R.string.playlist_popular_empty),
    FAVORITE(R.string.playlist_favorite, R.string.playlist_favorite_empty),
    NEW(R.string.playlist_new, R.string.playlist_new_empty),
    RECOMMENDATIONS(R.string.playlist_recommendations, R.string.playlist_recommendations_empty),
}

@Composable
fun DefaultPlaylistScreen(
    listBottomPadding: Dp,
    strategy: DefaultPlaylistScreenStrategy,
) {
    val viewModel: DefaultPlaylistViewModel = diViewModel()

    LaunchedEffect(viewModel, strategy) {
        viewModel.load(strategy)
    }

    val state by viewModel.state.collectAsState()

    ListScreen(
        title = stringResource(strategy.titleRes),
        listBottomPadding = listBottomPadding,
        emptyItemsContent = {
            if (state is DefaultPlaylistState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(strategy.emptyPlaceholderTextRes),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        },
        canReorder = strategy == DefaultPlaylistScreenStrategy.FAVORITE,
        onReorder = { from, to -> viewModel.reorder(from, to) },
        onCommitReorder = { from, to -> viewModel.commitReorder(from, to) },
        items = state.items,
        key = { it.id },
        contentType = { "UiStationElement" },
    ) { item ->
        StationItem(
            station = item.station,
            favorite = item.favorite,
            playingState = item.playingState,
            onPlayClick = { station, playingState -> viewModel.onPlayClick(station, playingState) },
            onClick = { station, playingState -> viewModel.onPlayClick(station, playingState) },
            onFavoriteClick = { station, isFavorite -> viewModel.onFavoriteClick(station, isFavorite) },
        )
    }
}
