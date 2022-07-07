package ru.debajo.srrradio.ui.list.reduktor

import android.content.Context
import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.ui.list.model.StationsListNews
import ru.debajo.srrradio.ui.list.model.StationsListState
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import java.util.*

class StationsListCommandResultReduktor(
    private val context: Context,
) : Reduktor<StationsListState, CommandResult, StationsListNews> {

    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(state, event)
            is ListenFavoriteStationsProcessor.Result -> reduceNewFavoriteStations(state, event)
            else -> Akt()
        }
    }

    private fun reduceSearchResult(
        state: StationsListState,
        event: SearchStationsCommandProcessor.SearchResult
    ): Akt<StationsListState, StationsListNews> {
        val playlist = UiPlaylist(
            id = UUID.randomUUID().toString(),
            name = "No named playlist",
            stations = event.stations,
        )
        return Akt(
            state = state.copy(
                playlist = playlist,
                uiElements = playlist.buildUiElements(context, state.favoriteStationsIds, state.mediaState)
            )
        )
    }

    private fun reduceOnNewMediaState(
        state: StationsListState,
        event: MediaStateListenerCommandProcessor.OnNewMediaState,
    ): Akt<StationsListState, StationsListNews> {
        return Akt(
            state = state.copy(
                mediaState = event.state,
                uiElements = state.playlist.buildUiElements(context, state.favoriteStationsIds, event.state)
            )
        )
    }

    private fun reduceNewFavoriteStations(
        state: StationsListState,
        event: ListenFavoriteStationsProcessor.Result
    ): Akt<StationsListState, StationsListNews> {
        val playlist = if (state.playlist == null || state.playlist.isFavorite) {
            event.stations.toFavoritePlaylist()
        } else {
            state.playlist
        }

        return Akt(
            state.copy(
                playlist = playlist,
                uiElements = playlist.buildUiElements(context, event.stations.map { it.id }.toSet(), state.mediaState),
                favoriteStations = event.stations
            )
        )
    }
}
