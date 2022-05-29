package ru.debajo.srrradio

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import ru.debajo.srrradio.di.AppApiHolder
import kotlin.math.roundToInt

class PlayerNotificationService : Service(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val radioPlayer: RadioPlayer by lazy { AppApiHolder.get().radioPlayer() }
    private val emptyBitmap: Bitmap by lazy { createEmptyBitmap() }
    private val receiver: PlaybackBroadcastReceiver by lazy { PlaybackBroadcastReceiver(radioPlayer) }

    override fun onBind(intent: Intent?): IBinder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        prepareChannel()
        launch {
            radioPlayer.states.flatMapLatest { state ->
                when (state) {
                    is RadioPlayer.State.HasStation -> buildNotification(state)
                    is RadioPlayer.State.None -> flowOf(null)
                }.map { notification -> notification to state }
            }.collect { (notification, state) ->
                if (notification == null || state !is RadioPlayer.State.HasStation) {
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

    private fun buildNotification(playerState: RadioPlayer.State.HasStation): Flow<Notification> {
        return playerState.station.image.observe().map { coverBitmap ->
            val style = androidx.media.app.NotificationCompat.MediaStyle()
            // TODO придумать, как это сделать красивее
            radioPlayer.mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, coverBitmap)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playerState.station.name)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, -1)
                    .build()
            )
            style.setMediaSession(radioPlayer.mediaSession.sessionToken)
            style.setShowActionsInCompactView(0)
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_radio)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(style)
                .run {
                    if (playerState.playWhenReady) {
                        addAction(R.drawable.ic_pause, null, PlaybackBroadcastReceiver.pauseIntent(this@PlayerNotificationService))
                    } else {
                        addAction(R.drawable.ic_play, null, PlaybackBroadcastReceiver.resumeIntent(this@PlayerNotificationService))
                    }
                }
                .build()
        }
    }

    private fun prepareChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val systemChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "context.getString(channel.name)",
                importance
            ).apply {
                this.description = "context.getString(channel.description)"
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

    private fun String?.observe(): Flow<Bitmap> {
        if (this.isNullOrEmpty()) {
            return flowOf(emptyBitmap)
        }

        return callbackFlow {
            val task = Glide
                .with(this@PlayerNotificationService)
                .asBitmap()
                .load(this@observe)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        trySend(emptyBitmap)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        trySend(resource)
                        return false
                    }

                })
                .submit()

            awaitClose { task.cancel(true) }
        }
    }

    private fun createEmptyBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#6c586b"))
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_radio)!!
        val drawableWidth = bitmap.width * 0.45f
        val drawableHeight = bitmap.height * 0.45f
        val drawableLeft = (bitmap.width - drawableWidth) / 2f
        val drawableTop = (bitmap.height - drawableHeight) / 2f
        drawable.setBounds(
            drawableLeft.roundToInt(),
            drawableTop.roundToInt(),
            (drawableLeft + drawableWidth).roundToInt(),
            (drawableTop + drawableHeight).roundToInt()
        )
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#907A88"))
        wrappedDrawable.draw(canvas)
        return bitmap
    }

    private class PlaybackBroadcastReceiver(
        private val radioPlayer: RadioPlayer,
    ) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> radioPlayer.pause()
                ACTION_RESUME -> radioPlayer.play()
            }
        }

        companion object {
            fun pauseIntent(context: Context): PendingIntent {
                return Intent(ACTION_PAUSE)
                    .setPackage(context.packageName)
                    .toPending(context, 0)
            }

            fun resumeIntent(context: Context): PendingIntent {
                return Intent(ACTION_RESUME)
                    .setPackage(context.packageName)
                    .toPending(context, 1)
            }

            fun intentFilter(): IntentFilter {
                return IntentFilter().apply {
                    addAction(ACTION_PAUSE)
                    addAction(ACTION_RESUME)
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