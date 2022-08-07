package ru.debajo.srrradio.ui.host.main.list.reduktor

import android.content.Context
import java.util.UUID
import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.R
import ru.debajo.srrradio.ui.host.main.list.model.StationsListNews
import ru.debajo.srrradio.ui.host.main.list.model.StationsListState
import ru.debajo.srrradio.ui.host.main.list.model.updateIdle
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.TrackCollectionListener

class StationsListCommandResultReduktor(
    private val context: Context,
) : Reduktor<StationsListState, CommandResult, StationsListNews> {

    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(state, event)
            is ListenFavoriteStationsProcessor.Result -> reduceNewFavoriteStations(state, event)
            is TrackCollectionListener.TrackCollectionChanged -> reduceTrackCollectionChanged(state, event)
            else -> Akt()
        }
    }

    private fun reduceSearchResult(
        state: StationsListState,
        event: SearchStationsCommandProcessor.SearchResult
    ): Akt<StationsListState, StationsListNews> {
        if (state !is StationsListState.InSearchMode) {
            return Akt()
        }

        val playlist = UiPlaylist(
            id = UUID.randomUUID().toString(),
            name = context.getString(R.string.search),
            stations = event.stations,
        )
        return Akt(
            state = state.copy(searchPlaylist = playlist)
        )
    }

    private fun reduceOnNewMediaState(
        state: StationsListState,
        event: MediaStateListenerCommandProcessor.OnNewMediaState,
    ): Akt<StationsListState, StationsListNews> {
        return Akt(
            state = state.updateIdle {
                copy(mediaState = event.state)
            }
        )
    }

    private fun reduceNewFavoriteStations(
        state: StationsListState,
        event: ListenFavoriteStationsProcessor.Result
    ): Akt<StationsListState, StationsListNews> {
        return Akt(
            state.updateIdle {
                copy(favoriteStations = event.stations)
            }
        )
    }

    private fun reduceTrackCollectionChanged(
        state: StationsListState,
        event: TrackCollectionListener.TrackCollectionChanged
    ): Akt<StationsListState, StationsListNews> {
        return Akt(
            state.updateIdle {
                copy(
                    collectionEmpty = event.collection.isEmpty()
                )
            }
        )
    }
}
