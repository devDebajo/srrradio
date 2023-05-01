package ru.debajo.srrradio.media

import android.content.Context
import android.media.audiofx.Equalizer
import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.debajo.srrradio.ProcessScope
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.service.PlayerNotificationService
import ru.debajo.srrradio.ui.model.UiStation

class RadioPlayer(
    private val context: Context,
    private val stationCoverLoader: StationCoverLoader,
    private val mediaSessionController: MediaSessionController,
    private val playerVolumePreference: PlayerVolumePreference,
    private val radioEqualizerPreference: RadioEqualizerPreference,
) {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val coroutineScope: LifecycleCoroutineScope by ProcessScope
    private val rendererFactory: FFTRendererFactory = FFTRendererFactory(context)
    private val emptyStationCoverListener: EmptyStationCoverListener = EmptyStationCoverListener(mediaSessionController, stationCoverLoader)

    var fftListener: FFTAudioProcessor.FFTListener? by rendererFactory::listener

    private val audioAttributes: AudioAttributes by lazy {
        AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
    }

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setRenderersFactory(rendererFactory)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        val state = statesMutable.value
                        if (state is State.HasStation) {
                            val title = mediaMetadata.extractTitle()
                            updateState(state.copy(playingTitle = title))
                        }
                    }
                })
            }
    }

    val equalizer: RadioEqualizer by lazy {
        RadioEqualizer(Equalizer(0, exoPlayer.audioSessionId))
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

    var volume: Float = 1f
        private set

    init {
        exoPlayer.addListener(object : Player.Listener {
            private val playWhenReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
            private val playbackState: MutableStateFlow<Int> = MutableStateFlow(Player.STATE_IDLE)

            init {
                coroutineScope.launch {
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
                        updateState(newState)
                        when (newState) {
                            is State.HasStation -> updateMediaSession(newState)
                            is State.None -> {
                                mediaSessionController.update { isActive = false }
                            }
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

        setVolume(playerVolumePreference.get())
        if (radioEqualizerPreference.hasValue) {
            equalizer.applyState(radioEqualizerPreference.get())
        }
    }

    fun saveEqualizerState() {
        radioEqualizerPreference.set(equalizer.dumpState())
    }

    fun setVolume(value: Float) {
        val normalized = value.coerceIn(0f, 1f)
        runOnUiThread { exoPlayer.volume = normalized }
        volume = normalized
        playerVolumePreference.set(normalized)
    }

    private suspend fun updateMediaSession(playerState: State.HasStation) = runCatchingNonCancellation {
        mediaSessionController.update {
            isActive = true
            station = playerState.station.name
            playingTitle = playerState.playingTitle
            cover = stationCoverLoader.emptyBitmap.value
            playbackState = playerState.playbackState
        }
        emptyStationCoverListener.listen = true

        val bitmap = stationCoverLoader.loadImage(playerState.station.image)
        if (bitmap != null) {
            emptyStationCoverListener.listen = false
            mediaSessionController.update {
                cover = bitmap
            }
        }
    }

    private var playPauseJob: Job? = null

    @AnyThread
    fun changeStation(station: UiStation, playWhenReady: Boolean = isPlaying) {
        playPauseJob?.cancel()
        playPauseJob = coroutineScope.launch(Main) {
            if (station.id == currentStation?.id) {
                if (playWhenReady) playAndSeekToEnd() else pause()
            } else {
                updateState(State.HasStation(station))
                exoPlayer.setMediaSource(mediaSourceFactory.createMediaSource(MediaItem.fromUri(station.stream)))
                exoPlayer.prepare()
                if (playWhenReady) {
                    playAndSeekToEnd()
                }
            }
        }
    }

    @AnyThread
    fun play() {
        playPauseJob?.cancel()
        playPauseJob = coroutineScope.launch(Main) {
            when (states.value) {
                is State.None -> Unit
                is State.HasStation -> playAndSeekToEnd()
            }
        }
    }

    @AnyThread
    fun toggle() {
        playPauseJob?.cancel()
        playPauseJob = coroutineScope.launch(Main) {
            when (val state = states.value) {
                is State.None -> Unit
                is State.HasStation -> {
                    when (state.playbackState) {
                        PlaybackState.PAUSED -> playAndSeekToEnd()
                        PlaybackState.PLAYING -> exoPlayer.pause()
                        PlaybackState.BUFFERING -> {
                            if (exoPlayer.playWhenReady) {
                                exoPlayer.pause()
                            } else {
                                playAndSeekToEnd()
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
        playPauseJob = coroutineScope.launch(Main) {
            when (states.value) {
                is State.None -> Unit
                is State.HasStation -> exoPlayer.pause()
            }
        }
    }

    private fun MediaMetadata.extractTitle(): String? {
        val title = title?.toString() ?: displayTitle?.toString()
        if (!title.isNullOrEmpty()) {
            return title
        }
        return null
    }

    private fun playAndSeekToEnd() {
        exoPlayer.seekTo(exoPlayer.bufferedPosition)
        exoPlayer.play()
    }

    private var startStopServiceJob: Job? = null

    private fun updateState(state: State) {
        statesMutable.value = state

        fun scheduleDeactivate(): Job {
            return coroutineScope.launch {
                delay(DEACTIVATE_DELAY_MS)
                mediaSessionController.update { isActive = false }
                PlayerNotificationService.stop(context)
            }
        }

        startStopServiceJob?.cancel()
        startStopServiceJob = when (state) {
            is State.HasStation -> {
                if (state.playbackState == PlaybackState.PAUSED) {
                    scheduleDeactivate()
                } else {
                    coroutineScope.launchWhenResumed { PlayerNotificationService.show(context) }
                }
            }
            is State.None -> scheduleDeactivate()
        }
    }

    private inline fun runOnUiThread(crossinline block: () -> Unit) {
        if (Looper.getMainLooper() === Looper.myLooper()) {
            block()
        } else {
            handler.post { block() }
        }
    }

    sealed interface State {
        object None : State

        data class HasStation(
            val station: UiStation,
            val playbackState: PlaybackState = PlaybackState.PAUSED,
            val playingTitle: String? = null,
        ) : State {
            val playing: Boolean
                get() = playbackState == PlaybackState.PLAYING
        }
    }

    enum class PlaybackState { PAUSED, BUFFERING, PLAYING, }

    private companion object {
        val DEACTIVATE_DELAY_MS = TimeUnit.MINUTES.toMillis(3)
    }
}

private class EmptyStationCoverListener(
    private val mediaSessionController: MediaSessionController,
    private val stationCoverLoader: StationCoverLoader,
) {

    private val coroutineScope: LifecycleCoroutineScope by ProcessScope

    var listen: Boolean = false

    init {
        coroutineScope.launch(Main) {
            stationCoverLoader.emptyBitmap.collect { emptyBitmap ->
                if (listen) {
                    mediaSessionController.update {
                        cover = emptyBitmap
                    }
                }
            }
        }
    }
}

