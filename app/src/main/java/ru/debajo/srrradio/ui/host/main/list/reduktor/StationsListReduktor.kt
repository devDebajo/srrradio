package ru.debajo.srrradio.ui.host.main.list.reduktor

import androidx.compose.ui.text.input.TextFieldValue
import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.R
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.ui.ext.Empty
import ru.debajo.srrradio.ui.ext.isEmpty
import ru.debajo.srrradio.ui.host.main.list.model.StationsListEvent
import ru.debajo.srrradio.ui.host.main.list.model.StationsListNews
import ru.debajo.srrradio.ui.host.main.list.model.StationsListState
import ru.debajo.srrradio.ui.host.main.list.model.playlist
import ru.debajo.srrradio.ui.host.main.list.model.toIdle
import ru.debajo.srrradio.ui.host.main.list.model.toSearch
import ru.debajo.srrradio.ui.host.main.list.model.updateIdle
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.model.toDomain
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.AppUpdateProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.PopularStationsProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.TrackCollectionListener
import ru.debajo.srrradio.ui.processor.UseFavoriteAsDefaultListener

class StationsListReduktor(
    private val lastStationUseCase: LastStationUseCase,
) : Reduktor<StationsListState, StationsListEvent, StationsListNews> {

    override fun invoke(state: StationsListState, event: StationsListEvent): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is StationsListEvent.Start -> reduceStart()
            is StationsListEvent.OnSearchQueryChanged -> reduceOnSearchQueryChanged(state, event)
            is StationsListEvent.ClearSearch -> reduceClearSearch(state)
            is StationsListEvent.OnPlayPauseStation -> reduceOnPlayPauseClick(state, event)
            is StationsListEvent.ChangeFavorite -> reduceChangeFavorite(event)
            is StationsListEvent.UpdateApp -> reduceUpdateApp(state)
        }
    }

    private fun reduceStart(): Akt<StationsListState, StationsListNews> {
        return Akt(
            commands = listOfNotNull(
                MediaStateListenerCommandProcessor.ListenerCommand.Start,
                ListenFavoriteStationsProcessor.Listen,
                TrackCollectionListener.Listen,
                PopularStationsProcessor.Load.takeIf { lastStationUseCase.lastPlaylistId == null },
                AppUpdateProcessor.Task.CheckUpdate,
                UseFavoriteAsDefaultListener.Listen,
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
            commands.add(SearchStationsCommandProcessor.Action.Search(event.query.text.trim()))
        }

        return Akt(
            state = newState,
            commands = commands,
        )
    }

    private fun reduceClearSearch(state: StationsListState): Akt<StationsListState, StationsListNews> {
        val next = reduceOnSearchQueryChanged(state, StationsListEvent.OnSearchQueryChanged(TextFieldValue.Empty))
        return next.copy(news = next.news + StationsListNews.ScrollToTop)
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
            commands = listOf(AddFavoriteStationProcessor.Update(event.station.toDomain(), event.favorite))
        )
    }

    private fun reduceUpdateApp(state: StationsListState): Akt<StationsListState, StationsListNews> {
        return Akt(
            state = state.updateIdle {
                copy(
                    hasAppUpdate = true,
                    loadingUpdate = true,
                    loadingProgress = 0f,
                )
            },
            commands = listOf(AppUpdateProcessor.Task.UpdateFlow),
            news = listOf(StationsListNews.ShowToast(R.string.update_downloading))
        )
    }
}
