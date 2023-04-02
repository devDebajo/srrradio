package ru.debajo.srrradio.ui.host.main.playlist

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Collections
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.ui.host.main.list.reduktor.stationPlayingState
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.UiStationElement
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.model.toDomain
import ru.debajo.srrradio.ui.model.toUi

@SuppressLint("StaticFieldLeak")
class DefaultPlaylistViewModel(
    private val context: Context,
    private val searchStationsUseCase: SearchStationsUseCase,
    private val favoriteStationsStateUseCase: FavoriteStationsStateUseCase,
    private val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase,
    private val mediaController: MediaController,
) : ViewModel() {

    private var job: Job? = null

    private val stateMutable: MutableStateFlow<DefaultPlaylistState> = MutableStateFlow(DefaultPlaylistState.Loading)
    val state: StateFlow<DefaultPlaylistState> = stateMutable.asStateFlow()

    fun load(strategy: DefaultPlaylistScreenStrategy) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            stateMutable.value = DefaultPlaylistState.Loading
            combine(
                loadInternal(strategy),
                mediaController.state,
                favoriteStationsStateUseCase.observe()
            ) { a, b, c -> transform(a.toPlaylist(name = strategy.title), b, c) }
                .collect { stateMutable.value = it }
        }
    }

    fun onPlayClick(station: UiStation, playingState: UiStationPlayingState) {
        val currentPlaylist = (stateMutable.value as? DefaultPlaylistState.Loaded)?.playlist ?: return
        viewModelScope.launch(Dispatchers.IO) {
            mediaController.newPlay(
                playlist = currentPlaylist,
                stationId = station.id,
                play = playingState != UiStationPlayingState.PLAYING,
            )
        }
    }

    fun onFavoriteClick(station: UiStation, favorite: Boolean) {
        viewModelScope.launch {
            updateFavoriteStationStateUseCase.update(station.toDomain(), favorite)
        }
    }

    fun reorder(from: Int, to: Int) {
        val state = stateMutable.value as? DefaultPlaylistState.Loaded ?: return
        val mutableList = state.items.toMutableList()
        Collections.swap(mutableList, from, to)
        stateMutable.value = state.copy(items = mutableList.toList())
    }

    fun commitReorder(from: Int, to: Int) {
        val state = stateMutable.value as? DefaultPlaylistState.Loaded ?: return
        viewModelScope.launch {
            val newStationsOrder = state.items.map { it.station.id }
            favoriteStationsStateUseCase.updateStations(newStationsOrder)
        }
    }

    private fun loadInternal(strategy: DefaultPlaylistScreenStrategy): Flow<List<Station>> {
        return when (strategy) {
            DefaultPlaylistScreenStrategy.NEW -> asFlow { searchStationsUseCase.searchNew(LIMIT) }

            DefaultPlaylistScreenStrategy.POPULAR -> asFlow { searchStationsUseCase.searchPopular(LIMIT) }

            DefaultPlaylistScreenStrategy.FAVORITE -> favoriteStationsStateUseCase.observe()
        }
    }

    private val DefaultPlaylistScreenStrategy.title: String
        get() = context.getString(titleRes)

    private fun transform(playlist: UiPlaylist, mediaState: MediaState, favoriteStations: List<Station>): DefaultPlaylistState {
        val favoriteIds = favoriteStations.map { it.id }.toSet()

        return DefaultPlaylistState.Loaded(
            playlist = playlist,
            items = playlist.stations.map { station ->
                UiStationElement(
                    station = station,
                    playingState = stationPlayingState(mediaState, station),
                    favorite = station.id in favoriteIds,
                )
            }
        )
    }

    private fun List<Station>.toPlaylist(
        id: String = UUID.randomUUID().toString(),
        name: String,
    ): UiPlaylist {
        return UiPlaylist(
            id = id,
            name = name,
            stations = map { it.toUi() },
        )
    }

    private fun <T> asFlow(block: suspend () -> T): Flow<T> {
        return flow { emit(block()) }
    }

    private companion object {
        const val LIMIT = 20
    }
}
