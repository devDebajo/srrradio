package ru.debajo.srrradio.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.srrradio.ProcessScope
import ru.debajo.srrradio.R
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.MediaSessionController
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.ui.host.HostActivity
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer

class PlayerNotificationService : Service(), CoroutineScope {

    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val mediaSessionController: MediaSessionController by lazy { AppApiHolder.get().mediaSessionController }
    private val receiver: PlaybackBroadcastReceiver by lazy { PlaybackBroadcastReceiver(mediaController) }
    private val coroutineScope: CoroutineScope by ProcessScope
    private val sleepTimer: SleepTimer by lazy { AppApiHolder.get().sleepTimer }

    override val coroutineContext: CoroutineContext = coroutineScope.coroutineContext + Job()

    override fun onBind(intent: Intent?): IBinder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        prepareChannel()
        startForeground(buildEmptyNotification())
        launch(Main) {
            mediaController.state
                .flatMapLatest { state -> observeNotification(state).map { notification -> notification to state } }
                .collect { (notification, state) ->
                    if (state !is MediaState.Loaded) {
                        stopListenPauseTask()
                    } else {
                        listenPauseTask()
                    }

                    updateNotification(notification)
                }
        }
        registerReceiver()
    }

    private var pauseTask: Job? = null

    private fun stopListenPauseTask() {
        pauseTask?.cancel()
        pauseTask = null
    }

    private fun listenPauseTask() {
        stopListenPauseTask()
        pauseTask = launch {
            sleepTimer.awaitPause {
                mediaController.pause()
            }
        }
    }

    private fun registerReceiver() {
        registerReceiver(receiver, PlaybackBroadcastReceiver.intentFilter())
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        unregisterReceiver(receiver)
    }

    private fun observeNotification(mediaState: MediaState): Flow<Notification> {
        return mediaSessionController.observe().map { mediaSession ->
            buildNotification(mediaState, mediaSession.sessionToken)
        }
    }

    private fun buildNotification(
        mediaState: MediaState,
        token: MediaSessionCompat.Token = mediaSessionController.mediaSession.sessionToken
    ): Notification {
        return when (mediaState) {
            is MediaState.Empty,
            is MediaState.Loading,
            is MediaState.None -> buildEmptyNotification()
            is MediaState.Loaded -> buildLoadedNotification(mediaState, token)
        }
    }

    private fun buildEmptyNotification(): Notification {
        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setMediaSession(null)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_radio)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(style)
            .setContentIntent(
                HostActivity.createIntent(this)
                    .toPending(this, 0, PendingIntentType.ACTIVITY)
            )
            .build()
    }

    private fun buildLoadedNotification(mediaState: MediaState.Loaded, token: MediaSessionCompat.Token): Notification {
        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setMediaSession(token)
        style.setShowActionsInCompactView(0, 1, 2)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_radio)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(style)
            .setContentIntent(
                HostActivity.createIntent(this)
                    .toPending(this, 0, PendingIntentType.ACTIVITY)
            )
            .apply {
                if (mediaState.hasPreviousStation) {
                    addAction(
                        R.drawable.ic_skip_previous,
                        R.string.accessibility_previous_station,
                        PlaybackBroadcastReceiver.previousIntent(this@PlayerNotificationService)
                    )
                } else {
                    addAction(R.drawable.ic_skip_previous, R.string.accessibility_previous_station)
                }
            }
            .run {
                when {
                    mediaState.playing -> addAction(
                        R.drawable.ic_pause,
                        R.string.accessibility_pause,
                        PlaybackBroadcastReceiver.pauseIntent(this@PlayerNotificationService)
                    )
                    mediaState.paused -> addAction(
                        R.drawable.ic_play,
                        R.string.accessibility_play,
                        PlaybackBroadcastReceiver.resumeIntent(this@PlayerNotificationService)
                    )
                    else -> addAction(R.drawable.ic_play, R.string.accessibility_play)
                }
            }
            .apply {
                if (mediaState.hasNextStation) {
                    addAction(
                        R.drawable.ic_skip_next,
                        R.string.accessibility_next_station,
                        PlaybackBroadcastReceiver.nextIntent(this@PlayerNotificationService)
                    )
                } else {
                    addAction(R.drawable.ic_skip_next, R.string.accessibility_next_station)
                }
            }
            .build()
    }

    private fun NotificationCompat.Builder.addAction(icon: Int, title: Int, intent: PendingIntent? = null): NotificationCompat.Builder {
        return addAction(icon, getString(title), intent)
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

    private fun updateNotification(notification: Notification) {
        notificationManager.notify(ID, notification)
    }

    class PlaybackBroadcastReceiver(
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
                    .toPending(context, 0, PendingIntentType.BROADCAST)
            }

            fun previousIntent(context: Context): PendingIntent {
                return Intent(ACTION_PREVIOUS)
                    .setPackage(context.packageName)
                    .toPending(context, 1, PendingIntentType.BROADCAST)
            }

            fun pauseIntent(context: Context): PendingIntent {
                return Intent(ACTION_PAUSE)
                    .setPackage(context.packageName)
                    .toPending(context, 2, PendingIntentType.BROADCAST)
            }

            fun resumeIntent(context: Context): PendingIntent {
                return Intent(ACTION_RESUME)
                    .setPackage(context.packageName)
                    .toPending(context, 2, PendingIntentType.BROADCAST)
            }

            fun intentFilter(): IntentFilter {
                return IntentFilter().apply {
                    addAction(ACTION_PAUSE)
                    addAction(ACTION_RESUME)
                    addAction(ACTION_PREVIOUS)
                    addAction(ACTION_NEXT)
                }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(createIntent(context))
            } else {
                context.startService(createIntent(context))
            }
        }

        fun stop(context: Context) {
            context.stopService(createIntent(context))
        }

        private fun createIntent(context: Context): Intent {
            return Intent(context, PlayerNotificationService::class.java)
        }
    }
}

private enum class PendingIntentType { BROADCAST, ACTIVITY }

private fun Intent.toPending(context: Context, requestCode: Int, type: PendingIntentType): PendingIntent {
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    return when (type) {
        PendingIntentType.BROADCAST -> PendingIntent.getBroadcast(context, requestCode, this, flags)
        PendingIntentType.ACTIVITY -> PendingIntent.getActivity(context, requestCode, this, flags)
    }
}
