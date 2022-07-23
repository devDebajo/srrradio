package ru.debajo.srrradio.media

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart

class MediaSessionController(private val context: Context) {

    private val scope: UpdateScopeImpl = UpdateScopeImpl()
    private val updates: MutableSharedFlow<Unit> = MutableSharedFlow()

    val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(context, "Srrradio media session")
    }

    fun observe(): Flow<MediaSessionCompat> {
        return combine(flowOf(mediaSession), updates.onStart { emit(Unit) }) { session, _ -> session }
    }

    suspend fun update(block: UpdateScope.() -> Unit) {
        scope.block()
        scope.applyTo(mediaSession)
        updates.emit(Unit)
    }

    interface UpdateScope {
        var isActive: Boolean
        var station: String
        var playingTitle: String?
        var cover: Bitmap?
        var playbackState: RadioPlayer.PlaybackState
        var hasNext: Boolean
        var hasPrevious: Boolean
    }

    private class UpdateScopeImpl : UpdateScope {
        override var isActive: Boolean = false
        override var station: String = ""
        override var playingTitle: String? = null
        override var cover: Bitmap? = null
        override var playbackState: RadioPlayer.PlaybackState = RadioPlayer.PlaybackState.PAUSED
        override var hasNext: Boolean = false
        override var hasPrevious: Boolean = false

        fun applyTo(mediaSession: MediaSessionCompat) {
            val metadata = createMediaMetadataCompat(station, playingTitle, cover)
            mediaSession.isActive = isActive
            mediaSession.setMetadata(metadata)

            val playState = when (playbackState) {
                RadioPlayer.PlaybackState.BUFFERING -> PlaybackStateCompat.STATE_BUFFERING
                RadioPlayer.PlaybackState.PLAYING -> PlaybackStateCompat.STATE_PLAYING
                else -> PlaybackStateCompat.STATE_PAUSED
            }
            var actions = 0L
            if (hasNext) {
                actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            }
            if (hasPrevious) {
                actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            }
            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(playState, 0L, 1f)
                    .setActions(actions)
                    .build()
            )
        }

        private fun createMediaMetadataCompat(stationName: String, title: String?, coverBitmap: Bitmap?): MediaMetadataCompat {
            val builder = MediaMetadataCompat.Builder()
            if (coverBitmap != null) {
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, coverBitmap)
            }

            if (title.isNullOrEmpty()) {
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, stationName)
            } else {
                builder
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, stationName)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            }

            return builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, -1).build()
        }
    }
}