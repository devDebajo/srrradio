package ru.debajo.srrradio.media

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.AnyThread
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.debajo.srrradio.service.PlayerNotificationService
import ru.debajo.srrradio.ui.model.UiStation

class RadioPlayer(
    private val context: Context,
    private val stationCoverLoader: StationCoverLoader,
    coroutineScope: CoroutineScope,
) : CoroutineScope by coroutineScope {

    private val audioAttributes: AudioAttributes by lazy {
        AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
    }

    private val exoPlayer: ExoPlayer by lazy {
        val player = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        MediaSessionConnector(mediaSession)
            .apply { setMediaButtonEventHandler(MediaButtonHandler()) }
            .setPlayer(player)
        player
    }

    private val mediaSourceFactory: ProgressiveMediaSource.Factory by lazy {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        ProgressiveMediaSource.Factory(dataSourceFactory)
    }

    private val currentStation: UiStation?
        get() {
            return when (val state = states.value) {
                is State.None -> null
                is State.HasStation -> state.station
            }
        }

    private val statesMutable: MutableStateFlow<State> = MutableStateFlow(State.None)
    val states: StateFlow<State> = statesMutable.asStateFlow()

    val isPlaying: Boolean
        get() = (states.value as? State.HasStation)?.playing == true

    val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(context, "Srrradio media session")
    }

    private val mediaSessionUpdatedMutable: MutableSharedFlow<Unit> = MutableSharedFlow()
    val mediaSessionUpdated: Flow<Unit> = mediaSessionUpdatedMutable.asSharedFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            private val playWhenReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
            private val playbackState: MutableStateFlow<Int> = MutableStateFlow(Player.STATE_IDLE)

            init {
                launch {
                    combine(playWhenReady, playbackState, statesMutable) { playWhenReady, playbackState, currentState ->
                        when (playbackState) {
                            Player.STATE_IDLE -> PlaybackState.PAUSED
                            Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                            Player.STATE_READY -> if (playWhenReady) PlaybackState.PLAYING else PlaybackState.PAUSED
                            Player.STATE_ENDED -> PlaybackState.PAUSED
                            else -> PlaybackState.PAUSED
                        } to currentState
                    }.collect { (playbackState, currentState) ->
                        val newState = when (currentState) {
                            is State.HasStation -> currentState.copy(playbackState = playbackState)
                            is State.None -> currentState
                        }
                        statesMutable.value = newState
                        if (newState is State.HasStation) {
                            updateMediaSession(newState)
                        }
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                this.playbackState.value = playbackState
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                this.playWhenReady.value = playWhenReady
            }
        })
    }

    private suspend fun updateMediaSession(playerState: State.HasStation) = runCatching {
        mediaSession.setMetadata(createMediaMetadataCompat(playerState.station.name, stationCoverLoader.emptyBitmap))
        val playbackState = when {
            playerState.buffering -> PlaybackStateCompat.STATE_BUFFERING
            playerState.playing -> PlaybackStateCompat.STATE_PLAYING
            else -> PlaybackStateCompat.STATE_PAUSED
        }
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(playbackState, 0L, 1f)
                .build()
        )
        mediaSessionUpdatedMutable.emit(Unit)

        val bitmap = stationCoverLoader.loadImage(playerState.station.image)
        if (bitmap != null) {
            mediaSession.setMetadata(createMediaMetadataCompat(playerState.station.name, bitmap))
            mediaSessionUpdatedMutable.emit(Unit)
        }
    }

    private var playPauseJob: Job? = null

    @AnyThread
    fun changeStation(station: UiStation, playWhenReady: Boolean = isPlaying) {
        playPauseJob?.cancel()
        playPauseJob = launch(Main) {
            if (station.id == currentStation?.id) {
                if (playWhenReady) play() else pause()
            } else {
                statesMutable.value = State.HasStation(station)
                exoPlayer.pause()
                exoPlayer.setMediaSource(mediaSourceFactory.createMediaSource(MediaItem.fromUri(station.stream)))
                exoPlayer.prepare()
                PlayerNotificationService.show(context)
                if (playWhenReady) {
                    exoPlayer.play()
                }
            }
        }
    }

    @AnyThread
    fun play() {
        playPauseJob?.cancel()
        playPauseJob = launch(Main) {
            when (states.value) {
                is State.None -> Unit
                is State.HasStation -> exoPlayer.play()
            }
        }
    }

    @AnyThread
    fun toggle() {
        playPauseJob?.cancel()
        playPauseJob = launch(Main) {
            when (val state = states.value) {
                is State.None -> Unit
                is State.HasStation -> {
                    when (state.playbackState) {
                        PlaybackState.PAUSED -> exoPlayer.play()
                        PlaybackState.PLAYING -> exoPlayer.pause()
                        PlaybackState.BUFFERING -> {
                            if (exoPlayer.playWhenReady) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        }
                    }
                }
            }
        }
    }

    @AnyThread
    fun pause() {
        playPauseJob?.cancel()
        playPauseJob = launch(Main) {
            when (states.value) {
                is State.None -> Unit
                is State.HasStation -> exoPlayer.pause()
            }
        }
    }

    private fun createMediaMetadataCompat(stationName: String, coverBitmap: Bitmap): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, coverBitmap)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, stationName)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, -1)
            .build()
    }

    sealed interface State {
        object None : State

        data class HasStation(
            val station: UiStation,
            val playbackState: PlaybackState = PlaybackState.PAUSED,
        ) : State {
            val playing: Boolean
                get() = playbackState == PlaybackState.PLAYING

            val buffering: Boolean
                get() = playbackState == PlaybackState.BUFFERING
        }
    }

    enum class PlaybackState {
        PAUSED,
        BUFFERING,
        PLAYING,
    }
}
