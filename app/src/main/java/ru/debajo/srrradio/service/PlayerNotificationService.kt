package ru.debajo.srrradio.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.srrradio.ProcessScope
import ru.debajo.srrradio.R
import ru.debajo.srrradio.common.utils.inject
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.MediaSessionController
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.ui.host.HostActivity
import ru.debajo.srrradio.ui.host.main.timer.SleepTimer

class PlayerNotificationService : Service(), CoroutineScope {

    private val notificationManager: NotificationManager by inject()
    private val mediaController: MediaController by inject()
    private val mediaSessionController: MediaSessionController by inject()
    private val coroutineScope: CoroutineScope by ProcessScope
    private val sleepTimer: SleepTimer by inject()

    override val coroutineContext: CoroutineContext = coroutineScope.coroutineContext + SupervisorJob()

    override fun onBind(intent: Intent?): IBinder? = null

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

    override fun onDestroy() {
        super.onDestroy()
        cancel()
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
            is MediaState.Loaded -> buildLoadedNotification(token)
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

    private fun buildLoadedNotification(token: MediaSessionCompat.Token): Notification {
        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setMediaSession(token)
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
