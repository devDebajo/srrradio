package ru.debajo.srrradio.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import ru.debajo.srrradio.R
import ru.debajo.srrradio.common.utils.hasPermission

class SrrradioNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager
) {
    private val permissionGranted: Boolean
        get() = hasNotificationPermission(context)

    @Composable
    fun RequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        ru.debajo.srrradio.ui.common.RequestPermission(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            key = this
        )
    }

    fun notify(id: Int, notification: Notification) {
        if (permissionGranted) {
            ensureChannelCreated(SrrradioNotificationChannel.from(notification))
            notificationManager.notify(id, notification)
        }
    }

    fun newNotificationBuilder(channel: SrrradioNotificationChannel): NotificationCompat.Builder {
        ensureChannelCreated(channel)
        return NotificationCompat.Builder(context, channel.id)
    }

    fun ensureChannelCreated(channel: SrrradioNotificationChannel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || channel == SrrradioNotificationChannel.StubChannel) {
            return
        }
        val channelCreated = runCatching { notificationManager.getNotificationChannel(channel.id) }
            .getOrNull() != null
        if (!channelCreated) {
            prepareChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareChannel(channel: SrrradioNotificationChannel) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val systemChannel = NotificationChannel(
            channel.id,
            context.getString(channel.nameRes),
            importance
        ).apply {
            this.description = context.getString(channel.descriptionRes)
        }
        notificationManager.createNotificationChannel(systemChannel)
    }

    companion object {
        fun hasNotificationPermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.hasPermission(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                true
            }
        }
    }
}

sealed interface SrrradioNotificationChannel {
    val id: String

    val nameRes: Int

    val descriptionRes: Int

    object MediaControls : SrrradioNotificationChannel {
        override val id: String = "SRRRADIO_MEDIA_PLAYBACK_NOTIFICATION"
        override val nameRes: Int = R.string.notification_channel_name
        override val descriptionRes: Int = R.string.notification_channel_description
    }

    object StubChannel : SrrradioNotificationChannel {
        override val id: String = ""
        override val nameRes: Int = 0
        override val descriptionRes: Int = 0
    }

    companion object {
        fun from(notification: Notification): SrrradioNotificationChannel {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return StubChannel
            }

            return when (notification.channelId) {
                MediaControls.id -> MediaControls
                else -> StubChannel
            }
        }
    }
}
