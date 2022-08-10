package ru.debajo.srrradio.media

import android.content.Intent
import android.view.KeyEvent
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import ru.debajo.srrradio.di.AppApiHolder

class MediaButtonHandler : MediaSessionConnector.MediaButtonEventHandler {

    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val mediaSessionController: MediaSessionController by lazy { AppApiHolder.get().mediaSessionController }

    override fun onMediaButtonEvent(player: Player, mediaButtonEvent: Intent): Boolean {
        if (mediaButtonEvent.action != Intent.ACTION_MEDIA_BUTTON) {
            return false
        }

        if (!mediaSessionController.mediaSession.isActive) {
            return false
        }

        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return false
        return when (event.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                mediaController.pause()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                mediaController.play()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                if (event.action == KeyEvent.ACTION_UP) {
                    mediaController.toggle()
                }
                true
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                mediaController.previous()
                true
            }
            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                mediaController.next()
                true
            }
            else -> false
        }
    }
}
