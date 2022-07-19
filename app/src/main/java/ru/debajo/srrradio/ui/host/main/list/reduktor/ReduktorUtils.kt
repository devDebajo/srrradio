package ru.debajo.srrradio.ui.host.main.list.reduktor

import android.content.Context
import ru.debajo.srrradio.R
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.ui.model.UiElement
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationElement
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.model.UiTextElement
import ru.debajo.srrradio.ui.model.toPlaylist

private const val FAVORITE_PLAYLIST_ID = "Favorite_id"
private const val FAVORITE_PLAYLIST_NAME = "Favorite"

fun UiPlaylist?.buildUiElements(
    context: Context,
    favoriteStationsIds: Set<String>,
    mediaState: MediaState?
): List<UiElement> {
    this ?: return emptyList()
    val result = stations.map { station ->
        UiStationElement(station, stationPlayingState(mediaState, station), station.id in favoriteStationsIds)
    }

    return if (id == FAVORITE_PLAYLIST_ID) {
        listOf(buildFavoriteElement(context)) + result
    } else {
        result
    }
}

val UiPlaylist.isFavorite: Boolean
    get() = id == FAVORITE_PLAYLIST_ID

fun List<UiStation>.toFavoritePlaylist(): UiPlaylist? {
    if (this.isEmpty()) {
        return null
    }
    return toPlaylist(id = FAVORITE_PLAYLIST_ID, name = FAVORITE_PLAYLIST_NAME)
}

private fun buildFavoriteElement(context: Context): UiElement {
    return UiTextElement(context.getString(R.string.favorite))
}

private fun stationPlayingState(mediaState: MediaState?, station: UiStation): UiStationPlayingState {
    val mediaStationInfo = (mediaState as? MediaState.Loaded)?.mediaStationInfo
    if (station.id != mediaStationInfo?.currentStationId) {
        return UiStationPlayingState.NONE
    }
    return mediaStationInfo.playingState
}
