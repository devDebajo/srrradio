package ru.debajo.srrradio.ui.host.main.list.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.Update
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.model.UiPlaylistIcon

object DefaultPlaylists {
    val NewStations = UiPlaylistIcon(
        title = R.string.playlist_new,
        icon = Icons.Rounded.Update,
    )

    val PopularStations = UiPlaylistIcon(
        title = R.string.playlist_popular,
        icon = Icons.Rounded.ShowChart,
    )

    val FavoriteStations = UiPlaylistIcon(
        title = R.string.playlist_favorite,
        icon = Icons.Rounded.FavoriteBorder,
    )

    val NearStations = UiPlaylistIcon(
        title = R.string.playlist_near,
        icon = Icons.Rounded.LocationOn,
    )

    val all: List<UiPlaylistIcon> = listOf(
        NewStations,
        PopularStations,
        FavoriteStations,
        NearStations,
    )
}
