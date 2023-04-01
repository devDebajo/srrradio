package ru.debajo.srrradio.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import ru.debajo.srrradio.media.MediaController

class PlaybackBroadcastReceiver(
    private val mediaController: MediaController,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PAUSE -> mediaController.pause()
            ACTION_RESUME -> mediaController.play()
            ACTION_NEXT -> mediaController.next()
            ACTION_PREVIOUS -> mediaController.previous()
            ACTION_CLOSE -> KillAppHelper.kill(context)
        }
    }

    companion object {
        fun nextIntent(context: Context): Intent {
            return Intent(ACTION_NEXT)
                .setPackage(context.packageName)
        }

        fun nextPending(context: Context): PendingIntent {
            return nextIntent(context)
                .toPending(context, 0, PendingIntentType.BROADCAST)
        }

        fun previousIntent(context: Context): Intent {
            return Intent(ACTION_PREVIOUS)
                .setPackage(context.packageName)
        }

        fun previousPending(context: Context): PendingIntent {
            return previousIntent(context)
                .toPending(context, 1, PendingIntentType.BROADCAST)
        }

        fun pauseIntent(context: Context): Intent {
            return Intent(ACTION_PAUSE)
                .setPackage(context.packageName)
        }

        fun pausePending(context: Context): PendingIntent {
            return pauseIntent(context)
                .toPending(context, 2, PendingIntentType.BROADCAST)
        }

        fun resumeIntent(context: Context): Intent {
            return Intent(ACTION_RESUME)
                .setPackage(context.packageName)
        }

        fun resumePending(context: Context): PendingIntent {
            return resumeIntent(context)
                .toPending(context, 2, PendingIntentType.BROADCAST)
        }

        fun closeIntent(context: Context): Intent {
            return Intent(ACTION_CLOSE)
                .setPackage(context.packageName)
        }

        fun closePending(context: Context): PendingIntent {
            return closeIntent(context)
                .toPending(context, 3, PendingIntentType.BROADCAST)
        }

        fun intentFilter(): IntentFilter {
            return IntentFilter().apply {
                addAction(ACTION_PAUSE)
                addAction(ACTION_RESUME)
                addAction(ACTION_PREVIOUS)
                addAction(ACTION_NEXT)
                addAction(ACTION_CLOSE)
            }
        }

        const val ACTION_PAUSE = "ru.debajo.srrradio.ACTION_PAUSE"
        const val ACTION_RESUME = "ru.debajo.srrradio.ACTION_RESUME"
        const val ACTION_PREVIOUS = "ru.debajo.srrradio.ACTION_PREVIOUS"
        const val ACTION_NEXT = "ru.debajo.srrradio.ACTION_NEXT"
        const val ACTION_CLOSE = "ru.debajo.srrradio.ACTION_CLOSE"
    }
}
