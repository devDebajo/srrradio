package ru.debajo.srrradio.ui.list.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.model.MediaState
import ru.debajo.srrradio.ui.list.model.StationsListNews
import ru.debajo.srrradio.ui.list.model.StationsListState
import ru.debajo.srrradio.ui.model.*
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import java.util.*

class StationsListCommandResultReduktor : Reduktor<StationsListState, CommandResult, StationsListNews> {
    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(state, event)
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
                uiElements = playlist.buildUiElements(state.mediaState)
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
                uiElements = state.playlist?.buildUiElements(event.state).orEmpty()
            )
        )
    }

    private fun UiPlaylist.buildUiElements(mediaState: MediaState?): List<UiElement> {
        return stations.map { station ->
            UiStationElement(station, stationPlayingState(mediaState, station))
        }
    }

    private fun stationPlayingState(mediaState: MediaState?, station: UiStation): UiStationPlayingState {
        val mediaStationInfo = (mediaState as? MediaState.Loaded)?.mediaStationInfo
        if (station.id != mediaStationInfo?.currentStationId) {
            return UiStationPlayingState.NONE
        }
        return mediaStationInfo.playingState
    }
}
