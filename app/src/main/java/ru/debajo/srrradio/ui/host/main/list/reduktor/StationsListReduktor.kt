package ru.debajo.srrradio.ui.host.main.list.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.ui.host.main.list.model.StationsListEvent
import ru.debajo.srrradio.ui.host.main.list.model.StationsListNews
import ru.debajo.srrradio.ui.host.main.list.model.StationsListState
import ru.debajo.srrradio.ui.host.main.list.model.playlist
import ru.debajo.srrradio.ui.host.main.list.model.toIdle
import ru.debajo.srrradio.ui.host.main.list.model.toSearch
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.TrackCollectionListener

class StationsListReduktor : Reduktor<StationsListState, StationsListEvent, StationsListNews> {

    override fun invoke(state: StationsListState, event: StationsListEvent): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is StationsListEvent.Start -> reduceStart()
            is StationsListEvent.OnSearchQueryChanged -> reduceOnSearchQueryChanged(state, event)
            is StationsListEvent.OnPlayPauseStation -> reduceOnPlayPauseClick(state, event)
            is StationsListEvent.ChangeFavorite -> reduceChangeFavorite(event)
        }
    }

    private fun reduceStart(): Akt<StationsListState, StationsListNews> {
        return Akt(
            commands = listOf(
                MediaStateListenerCommandProcessor.ListenerCommand.Start,
                ListenFavoriteStationsProcessor.Listen,
                TrackCollectionListener.Listen,
            )
        )
    }

    private fun reduceOnSearchQueryChanged(
        state: StationsListState,
        event: StationsListEvent.OnSearchQueryChanged
    ): Akt<StationsListState, StationsListNews> {
        var newState: StationsListState = state.toSearch { copy(searchQuery = event.query) }
        val commands = mutableListOf<Command>()
        if (event.query.isEmpty()) {
            newState = newState.toIdle()
            commands.add(SearchStationsCommandProcessor.Action.Cancel)
        } else {
            commands.add(SearchStationsCommandProcessor.Action.Search(event.query))
        }

        return Akt(
            state = newState,
            commands = commands,
        )
    }

    private fun reduceOnPlayPauseClick(
        state: StationsListState,
        event: StationsListEvent.OnPlayPauseStation,
    ): Akt<StationsListState, StationsListNews> {
        val playlist = state.playlist ?: return Akt()
        return Akt(
            commands = listOf(
                NewPlayCommandProcessor.NewPlay(
                    playlist = playlist,
                    stationId = event.station.id,
                    play = event.playingState != UiStationPlayingState.PLAYING,
                )
            )
        )
    }

    private fun reduceChangeFavorite(event: StationsListEvent.ChangeFavorite): Akt<StationsListState, StationsListNews> {
        return Akt(
            commands = listOf(AddFavoriteStationProcessor.Update(event.station.id, event.favorite))
        )
    }
}
