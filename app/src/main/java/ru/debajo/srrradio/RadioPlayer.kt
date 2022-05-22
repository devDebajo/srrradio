package ru.debajo.srrradio

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.debajo.srrradio.ui.model.UiStation

class RadioPlayer(private val context: Context) {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val audioAttributes: AudioAttributes by lazy {
        AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
    }
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build()
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

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val localPlaybackState = when (playbackState) {
                    Player.STATE_IDLE -> PlaybackState.IDLE
                    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                    Player.STATE_READY -> PlaybackState.READY
                    Player.STATE_ENDED -> PlaybackState.ENDED
                    else -> PlaybackState.IDLE
                }
                statesMutable.value = when (val state = statesMutable.value) {
                    is State.HasStation -> state.copy(playbackState = localPlaybackState)
                    is State.None -> return
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                statesMutable.value = when (val state = statesMutable.value) {
                    is State.HasStation -> state.copy(playWhenReady = playWhenReady)
                    is State.None -> return
                }
            }
        })
    }

    @AnyThread
    fun changeStation(station: UiStation, playWhenReady: Boolean = isPlaying) = runOnMainThread {
        if (station == currentStation) {
            if (playWhenReady) play() else pause()
        } else {
            exoPlayer.pause()
            exoPlayer.setMediaSource(mediaSourceFactory.createMediaSource(MediaItem.fromUri(station.stream)))
            exoPlayer.prepare()
            statesMutable.value = State.HasStation(station)
            if (playWhenReady) {
                exoPlayer.play()
            }
        }
    }

    @AnyThread
    fun play() = runOnMainThread {
        when (states.value) {
            is State.None -> Unit
            is State.HasStation -> exoPlayer.play()
        }
    }

    @AnyThread
    fun pause() = runOnMainThread {
        when (states.value) {
            is State.None -> Unit
            is State.HasStation -> exoPlayer.pause()
        }
    }

    private inline fun runOnMainThread(crossinline block: () -> Unit) {
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
            val playbackState: PlaybackState = PlaybackState.IDLE,
            val playWhenReady: Boolean = false,
        ) : State {
            val playing: Boolean
                get() = playbackState == PlaybackState.READY && playWhenReady

            val buffering: Boolean
                get() = playbackState == PlaybackState.BUFFERING
        }
    }

    enum class PlaybackState {
        IDLE,
        BUFFERING,
        READY,
        ENDED
    }
}
