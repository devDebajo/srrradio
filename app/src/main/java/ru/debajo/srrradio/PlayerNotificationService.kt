package ru.debajo.srrradio

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.model.MediaState
import kotlin.coroutines.CoroutineContext

class PlayerNotificationService : Service(), CoroutineScope {

    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val receiver: PlaybackBroadcastReceiver by lazy { PlaybackBroadcastReceiver(mediaController) }
    private val coroutineScope: CoroutineScope by lazy { AppApiHolder.get().coroutineScope }

    override val coroutineContext: CoroutineContext
        get() = coroutineScope.coroutineContext

    override fun onBind(intent: Intent?): IBinder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        prepareChannel()
        launch(Main) {
            mediaController.state.flatMapLatest { state ->
                when (state) {
                    MediaState.Empty,
                    MediaState.Loading,
                    MediaState.None -> flowOf(null)
                    is MediaState.Loaded -> buildNotification(state)
                }.map { notification -> notification to state }
            }.collect { (notification, state) ->
                if (notification == null || state !is MediaState.Loaded) {
                    stopForeground(true)
                } else {
                    startForeground(notification)
                }
            }
        }
        registerReceiver()
    }

    private fun registerReceiver() {
        registerReceiver(receiver, PlaybackBroadcastReceiver.intentFilter())
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        unregisterReceiver(receiver)
    }

    private fun androidx.media.app.NotificationCompat.MediaStyle.setShowActionsInCompactView(mediaState: MediaState.Loaded): androidx.media.app.NotificationCompat.MediaStyle {
        var lastAction = 0
        val visibleActions = mutableListOf<Int>()
        if (mediaState.hasPreviousStation) {
            visibleActions.add(lastAction++)
        }
        if (mediaState.playing || mediaState.paused) {
            visibleActions.add(lastAction++)
        }
        if (mediaState.hasNextStation) {
            visibleActions.add(lastAction)
        }
        return setShowActionsInCompactView(*visibleActions.toIntArray())
    }

    private fun buildNotification(mediaState: MediaState.Loaded): Flow<Notification> {
        return mediaController.mediaSession.map { mediaSession ->
            val style = androidx.media.app.NotificationCompat.MediaStyle()
            style.setMediaSession(mediaSession.sessionToken)
            style.setShowActionsInCompactView(mediaState)
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_radio)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(style)
                .run {
                    if (mediaState.hasPreviousStation) {
                        addAction(
                            R.drawable.ic_skip_previous,
                            getString(R.string.accessibility_previous_station),
                            PlaybackBroadcastReceiver.previousIntent(this@PlayerNotificationService)
                        )
                    }
                    this
                }
                .run {
                    when {
                        mediaState.playing -> addAction(
                            R.drawable.ic_pause,
                            getString(R.string.accessibility_pause),
                            PlaybackBroadcastReceiver.pauseIntent(this@PlayerNotificationService)
                        )
                        mediaState.paused -> addAction(
                            R.drawable.ic_play,
                            getString(R.string.accessibility_play),
                            PlaybackBroadcastReceiver.resumeIntent(this@PlayerNotificationService)
                        )
                        else -> this
                    }
                }
                .run {
                    if (mediaState.hasNextStation) {
                        addAction(
                            R.drawable.ic_skip_next,
                            getString(R.string.accessibility_next_station),
                            PlaybackBroadcastReceiver.nextIntent(this@PlayerNotificationService)
                        )
                    }
                    this
                }
                .build()
        }
    }

    private fun prepareChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val systemChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                importance
            ).apply {
                this.description = getString(R.string.notification_channel_description)
            }
            notificationManager.createNotificationChannel(systemChannel)
        }
    }

    private fun startForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(ID, notification)
        }
    }

    private class PlaybackBroadcastReceiver(
        private val mediaController: MediaController,
    ) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> mediaController.pause()
                ACTION_RESUME -> mediaController.play()
                ACTION_NEXT -> mediaController.next()
                ACTION_PREVIOUS -> mediaController.previous()
            }
        }

        companion object {
            fun nextIntent(context: Context): PendingIntent {
                return Intent(ACTION_NEXT)
                    .setPackage(context.packageName)
                    .toPending(context, 0)
            }

            fun previousIntent(context: Context): PendingIntent {
                return Intent(ACTION_PREVIOUS)
                    .setPackage(context.packageName)
                    .toPending(context, 1)
            }

            fun pauseIntent(context: Context): PendingIntent {
                return Intent(ACTION_PAUSE)
                    .setPackage(context.packageName)
                    .toPending(context, 2)
            }

            fun resumeIntent(context: Context): PendingIntent {
                return Intent(ACTION_RESUME)
                    .setPackage(context.packageName)
                    .toPending(context, 2)
            }

            fun intentFilter(): IntentFilter {
                return IntentFilter().apply {
                    addAction(ACTION_PAUSE)
                    addAction(ACTION_RESUME)
                    addAction(ACTION_PREVIOUS)
                    addAction(ACTION_NEXT)
                }
            }

            private fun Intent.toPending(context: Context, requestCode: Int): PendingIntent {
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                return PendingIntent.getBroadcast(context, requestCode, this, flags)
            }

            private const val ACTION_PAUSE = "ru.debajo.srrradio.ACTION_PAUSE"
            private const val ACTION_RESUME = "ru.debajo.srrradio.ACTION_RESUME"
            private const val ACTION_PREVIOUS = "ru.debajo.srrradio.ACTION_PREVIOUS"
            private const val ACTION_NEXT = "ru.debajo.srrradio.ACTION_NEXT"
        }
    }

    companion object {
        private const val ID = 45463725
        private const val NOTIFICATION_CHANNEL_ID = "SRRRADIO_MEDIA_PLAYBACK_NOTIFICATION"

        fun show(context: Context) {
            context.startService(createIntent(context))
        }

        fun createIntent(context: Context): Intent {
            return Intent(context, PlayerNotificationService::class.java)
        }
    }
}