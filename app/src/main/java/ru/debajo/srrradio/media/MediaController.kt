package ru.debajo.srrradio.media

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import java.util.UUID
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.debajo.srrradio.ProcessScopeImmediate
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.media.model.MediaStationInfo
import ru.debajo.srrradio.media.model.asLoaded
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.model.toDomain
import ru.debajo.srrradio.ui.model.toUi

class MediaController(
    private val player: RadioPlayer,
    private val lastStationUseCase: LastStationUseCase,
    private val loadPlaylistUseCase: LoadPlaylistUseCase,
    private val mediaSessionController: MediaSessionController,
) : MediaActions, CoroutineScope by ProcessScopeImmediate, MediaSessionCompat.Callback() {

    private val stateMutable: MutableStateFlow<MediaState> = MutableStateFlow(MediaState.None)
    val state: StateFlow<MediaState> = stateMutable.asStateFlow()

    init {
        mediaSessionController.mediaSession.setCallback(this)
    }

    suspend fun prepare() {
        if (stateMutable.value !is MediaState.None) {
            return
        }

        stateMutable.value = MediaState.Loading
        val firstState = calculateFirstState()
        stateMutable.value = firstState

        val currentStation = (firstState as? MediaState.Loaded)?.currentStation
        if (currentStation != null) {
            player.changeStation(currentStation, false)
        }

        player.states.collect { playerState -> onNewPlayerState(playerState) }
    }

    override fun pause() {
        player.pause()
    }

    override fun play() {
        player.play()
    }

    override fun toggle() {
        player.toggle()
    }

    override fun onPlay() {
        play()
    }

    override fun onPause() {
        pause()
    }

    override fun onSkipToNext() {
        next()
    }

    override fun onSkipToPrevious() {
        previous()
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        if (action == MediaSessionController.ACTION_CLOSE) {
            exitProcess(0)
        } else {
            super.onCustomAction(action, extras)
        }
    }

    override fun next() {
        val currentMediaState = stateMutable.value as? MediaState.Loaded ?: return
        val nextStation = currentMediaState.playlist.stations.getOrNull(
            currentMediaState.currentStationIndex + 1
        ) ?: return
        stateMutable.value = currentMediaState.copy(
            mediaStationInfo = currentMediaState.mediaStationInfo?.copy(
                playingState = UiStationPlayingState.PLAYING,
                currentStationId = nextStation.id
            )
        )
        player.changeStation(nextStation, true)
        savePlaylistInfoToPrefs()
    }

    override fun previous() {
        val currentMediaState = stateMutable.value as? MediaState.Loaded ?: return
        val previousStation = currentMediaState.playlist.stations.getOrNull(
            currentMediaState.currentStationIndex - 1
        ) ?: return
        stateMutable.value = currentMediaState.copy(
            mediaStationInfo = currentMediaState.mediaStationInfo?.copy(
                playingState = UiStationPlayingState.PLAYING,
                currentStationId = previousStation.id,
            )
        )
        player.changeStation(previousStation, true)
        savePlaylistInfoToPrefs()
    }

    suspend fun newPlay(playlist: UiPlaylist, stationId: String, play: Boolean) {
        val station = playlist.stations.first { it.id == stationId }
        loadPlaylistUseCase.createOrUpdate(playlist.toDomain())

        stateMutable.value = MediaState.Loaded(
            playlist = playlist,
            mediaStationInfo = MediaStationInfo(stationId, UiStationPlayingState.NONE, null)
        )

        savePlaylistInfoToPrefs()

        player.changeStation(station, play)
    }

    fun changeStation(stationId: String, play: Boolean) {
        val state = state.value.asLoaded ?: return
        val station = state.playlist.stations.firstOrNull { it.id == stationId } ?: return

        stateMutable.value = state.copy(
            mediaStationInfo = MediaStationInfo(stationId, UiStationPlayingState.NONE, null)
        )

        savePlaylistInfoToPrefs()

        player.changeStation(station, play)
    }

    private fun savePlaylistInfoToPrefs() {
        val currentMediaState = stateMutable.value as? MediaState.Loaded ?: return
        lastStationUseCase.lastPlaylistId = currentMediaState.playlist.id
        lastStationUseCase.lastStationId = currentMediaState.currentStation?.id
    }

    private suspend fun calculateFirstState(): MediaState {
        val lastPlaylistId = lastStationUseCase.lastPlaylistId ?: return MediaState.Empty
        val playlist = loadPlaylistUseCase.loadPlaylist(lastPlaylistId) ?: return MediaState.Empty
        val lastStation = lastStationUseCase.lastStationId?.let { lastStationId ->
            playlist.stations.firstOrNull { it.id == lastStationId }
        }
        return MediaState.Loaded(
            playlist = playlist.toUi(),
            mediaStationInfo = lastStation?.let { MediaStationInfo(it.id, UiStationPlayingState.NONE, null) }
        )
    }

    private suspend fun onNewPlayerState(playerState: RadioPlayer.State) {
        val currentMediaState = stateMutable.value as? MediaState.Loaded ?: return
        stateMutable.value = when (playerState) {
            is RadioPlayer.State.None -> {
                currentMediaState.copy(
                    mediaStationInfo = currentMediaState.mediaStationInfo?.copy(
                        playingState = UiStationPlayingState.NONE
                    )
                )
            }
            is RadioPlayer.State.HasStation -> {
                if (playerState.station !in currentMediaState.playlist) {
                    MediaState.Loaded(
                        playlist = UiPlaylist(UUID.randomUUID().toString(), "", listOf(playerState.station)),
                        mediaStationInfo = MediaStationInfo(
                            currentStationId = playerState.station.id,
                            playingState = playerState.playbackState.toUi(),
                            title = playerState.playingTitle
                        ),
                    )
                } else {
                    currentMediaState.copy(
                        mediaStationInfo = MediaStationInfo(
                            currentStationId = playerState.station.id,
                            playingState = playerState.playbackState.toUi(),
                            title = playerState.playingTitle
                        )
                    )
                }
            }
        }.updateMediaSession()
    }

    private suspend fun MediaState.Loaded.updateMediaSession(): MediaState.Loaded {
        mediaSessionController.update {
            hasNext = hasNextStation
            hasPrevious = hasPreviousStation
        }
        return this
    }
}

interface MediaActions {
    fun pause()
    fun play()
    fun toggle()

    fun next()
    fun previous()
}
