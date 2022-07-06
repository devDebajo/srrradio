package ru.debajo.srrradio.ui.list.reduktor

import android.content.Context
import ru.debajo.srrradio.R
import ru.debajo.srrradio.model.MediaState
import ru.debajo.srrradio.ui.model.*

private const val FAVORITE_PLAYLIST_ID = "Favorite_id"
private const val FAVORITE_PLAYLIST_NAME = "Favorite"

fun UiPlaylist?.buildUiElements(context: Context, mediaState: MediaState?): List<UiElement> {
    this ?: return emptyList()
    val result = stations.map { station ->
        UiStationElement(station, stationPlayingState(mediaState, station))
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
