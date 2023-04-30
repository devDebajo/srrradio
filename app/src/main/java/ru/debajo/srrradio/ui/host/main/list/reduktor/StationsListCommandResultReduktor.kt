package ru.debajo.srrradio.ui.host.main.list.reduktor

import android.content.Context
import java.util.UUID
import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.R
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.ui.host.main.list.model.StationsListNews
import ru.debajo.srrradio.ui.host.main.list.model.StationsListState
import ru.debajo.srrradio.ui.host.main.list.model.idle
import ru.debajo.srrradio.ui.host.main.list.model.playlist
import ru.debajo.srrradio.ui.host.main.list.model.updateIdle
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.toPlaylist
import ru.debajo.srrradio.ui.processor.AppUpdateProcessor
import ru.debajo.srrradio.ui.processor.AutoplayProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.PopularStationsProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.TrackCollectionListener
import ru.debajo.srrradio.ui.processor.UseFavoriteAsDefaultListener

class StationsListCommandResultReduktor(
    private val context: Context,
) : Reduktor<StationsListState, CommandResult, StationsListNews> {

    override fun invoke(state: StationsListState, event: CommandResult): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is SearchStationsCommandProcessor.SearchResult -> reduceSearchResult(state, event)
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(state, event)
            is ListenFavoriteStationsProcessor.Result -> reduceNewFavoriteStations(state, event)
            is TrackCollectionListener.TrackCollectionChanged -> reduceTrackCollectionChanged(state, event)
            is PopularStationsProcessor.Loaded -> reducePopularStationsLoaded(state, event)
            is AppUpdateProcessor.Result -> reduceAppUpdateProcessorResult(state, event)
            is UseFavoriteAsDefaultListener.OnUseFavoriteAsDefaultChanged -> reduceOnUseFavoriteAsDefaultChanged(state, event)
            else -> Akt()
        }
    }

    private fun reducePopularStationsLoaded(
        state: StationsListState,
        event: PopularStationsProcessor.Loaded,
    ): Akt<StationsListState, StationsListNews> {
        if (state.playlist != null) {
            return Akt()
        }

        return Akt(
            state.updateIdle {
                copy(
                    fallBackPlaylist = event.stations.toPlaylist(
                        name = context.getString(R.string.playlist_popular),
                    )
                )
            }
        )
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
            name = "${context.getString(R.string.search)}: ${event.query}",
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
        var commands: List<Command>? = null
        var autoPlayed = state.idle.autoPlayed
        if (event.state is MediaState.Loaded && event.state.mediaStationInfo != null && event.state.mediaStationInfo.playerInitialized && !autoPlayed) {
            commands = listOf(AutoplayProcessor.Autoplay)
            autoPlayed = true
        }
        return Akt(
            state = state.updateIdle {
                copy(
                    mediaState = event.state,
                    autoPlayed = autoPlayed,
                )
            },
            commands = commands ?: emptyList(),
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

    private fun reduceAppUpdateProcessorResult(
        state: StationsListState,
        event: AppUpdateProcessor.Result
    ): Akt<StationsListState, StationsListNews> {
        return when (event) {
            is AppUpdateProcessor.Result.HasUpdate -> {
                Akt(state = state.updateIdle { copy(hasAppUpdate = true) })
            }
            is AppUpdateProcessor.Result.UpdateSuccess -> {
                Akt(
                    state = state.updateIdle {
                        copy(
                            hasAppUpdate = false,
                            loadingUpdate = false,
                            loadingProgress = 0f
                        )
                    },
                    news = listOf(StationsListNews.ShowToast(R.string.update_success))
                )
            }
            is AppUpdateProcessor.Result.UpdateFailed -> {
                Akt(
                    state = state.updateIdle {
                        copy(
                            hasAppUpdate = true,
                            loadingUpdate = false,
                            loadingProgress = 0f
                        )
                    },
                    news = listOf(StationsListNews.ShowToast(R.string.update_failed))
                )
            }
            is AppUpdateProcessor.Result.LoadingUpdate -> {
                Akt(
                    state = state.updateIdle {
                        copy(
                            hasAppUpdate = true,
                            loadingUpdate = true,
                            loadingProgress = event.progress
                        )
                    }
                )
            }
        }
    }

    private fun reduceOnUseFavoriteAsDefaultChanged(
        state: StationsListState,
        event: UseFavoriteAsDefaultListener.OnUseFavoriteAsDefaultChanged,
    ): Akt<StationsListState, StationsListNews> {
        return Akt(
            state = state.updateIdle {
                copy(useFavoritePlaylistAsDefault = event.value)
            }
        )
    }
}
