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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.debajo.srrradio.di.AppApiHolder
import ru.debajo.srrradio.model.MediaState
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class PlayerNotificationService : Service(), CoroutineScope {

    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val mediaController: MediaController by lazy { AppApiHolder.get().mediaController }
    private val emptyBitmap: Bitmap by lazy { createEmptyBitmap() }
    private val receiver: PlaybackBroadcastReceiver by lazy { PlaybackBroadcastReceiver(mediaController) }
    private val coroutineScope: CoroutineScope by lazy { AppApiHolder.get().coroutineScope }

    override val coroutineContext: CoroutineContext
        get() = coroutineScope.coroutineContext

    override fun onBind(intent: Intent?): IBinder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        prepareChannel()
        launch {
            mediaController.state.mapLatest { state ->
                when (state) {
                    MediaState.Empty,
                    MediaState.Loading,
                    MediaState.None -> null
                    is MediaState.Loaded -> buildNotification(state)
                } to state
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

    private fun buildNotification(mediaState: MediaState.Loaded): Notification {
        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setMediaSession(mediaController.mediaSession.sessionToken)
        style.setShowActionsInCompactView(0)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_radio)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(style)
            .run {
                when {
                    mediaState.playing -> addAction(
                        R.drawable.ic_pause,
                        null,
                        PlaybackBroadcastReceiver.pauseIntent(this@PlayerNotificationService)
                    )
                    !mediaState.playing && !mediaState.buffering -> addAction(
                        R.drawable.ic_play,
                        null,
                        PlaybackBroadcastReceiver.resumeIntent(this@PlayerNotificationService)
                    )
                    else -> this
                }
            }
            .build()
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
        private val mediaController: MediaController,
    ) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PAUSE -> mediaController.pause()
                ACTION_RESUME -> mediaController.play()
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