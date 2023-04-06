package ru.debajo.srrradio.ui.host.main.list.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Recommend
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Upgrade
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.model.UiMainTile

object DefaultMainTiles {
    val UpdateApp = UiMainTile(
        title = R.string.tile_has_update,
        icon = Icons.Rounded.Upgrade,
    )

    val RecommendedStations = UiMainTile(
        title = R.string.playlist_recommendations,
        icon = Icons.Rounded.Recommend,
    )

    val NewStations = UiMainTile(
        title = R.string.playlist_new,
        icon = Icons.Rounded.Update,
    )

    val PopularStations = UiMainTile(
        title = R.string.playlist_popular,
        icon = Icons.Rounded.ShowChart,
    )

    val FavoriteStations = UiMainTile(
        title = R.string.playlist_favorite,
        icon = Icons.Rounded.FavoriteBorder,
    )

    val StationsOnMap = UiMainTile(
        title = R.string.playlist_on_map,
        icon = Icons.Rounded.Map,
    )

    fun getTiles(hasAppUpdate: Boolean): List<UiMainTile> {
        return listOfNotNull(
            UpdateApp.takeIf { hasAppUpdate },
            NewStations,
            PopularStations,
            FavoriteStations,
            RecommendedStations,
            StationsOnMap,
        )
    }
}
