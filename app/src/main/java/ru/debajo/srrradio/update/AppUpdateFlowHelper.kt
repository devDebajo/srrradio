package ru.debajo.srrradio.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.debajo.srrradio.BuildConfig
import ru.debajo.srrradio.R
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.domain.repository.ConfigRepository

class AppUpdateFlowHelper(
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val configRepository: ConfigRepository,
) {
    suspend fun updateApp(): Boolean {
        val updateFileUrl = configRepository.provide().updateFileUrl ?: return false
        val request = buildDownloadRequest(Uri.parse(updateFileUrl))
        val downloadId = downloadManager.enqueue(request)
        val downloadResult = awaitDownload(downloadId) ?: return false
        val downloadedUri = prepareUriForIntent(downloadResult.uri) ?: return false
        val intent = buildIntent(downloadedUri)
        return runCatching { context.startActivity(intent) }.map { true }.getOrElse { false }
    }

    private fun buildDownloadRequest(uri: Uri): DownloadManager.Request {
        return DownloadManager.Request(uri)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setTitle(context.getString(R.string.update_downloading))
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "srrradio-update.apk")
    }

    private fun prepareUriForIntent(uri: String): Uri? {
        return runCatching {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                File(Uri.parse(uri).path!!)
            )
        }.toTimber().getOrNull()
    }

    private fun buildIntent(downloadedUri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            .setData(downloadedUri)
    }

    @SuppressLint("Range")
    private suspend fun awaitDownload(downloadId: Long): DownloadResult? {
        context.awaitReceiver(DownloadManager.ACTION_DOWNLOAD_COMPLETE) { intent ->
            intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) == downloadId
        }

        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        try {
            if (cursor.moveToFirst()) {
                val downloadStatus: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val downloadLocalUri: String? = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                val downloadMimeType: String? = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                    return DownloadResult(uri = downloadLocalUri, mimeType = downloadMimeType)
                }
            }
        } catch (e: Throwable) {
            return null
        } finally {
            cursor.close()
        }
        return null
    }

    private suspend fun Context.awaitReceiver(action: String, filter: (Intent) -> Boolean): Intent {
        return suspendCancellableCoroutine { continuation ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == action && filter(intent)) {
                        unregisterReceiver(this)
                        continuation.resumeWith(Result.success(intent))
                    }
                }
            }

            registerReceiver(receiver, IntentFilter(action))
            continuation.invokeOnCancellation { unregisterReceiver(receiver) }
        }
    }

    private class DownloadResult(
        val uri: String,
        val mimeType: String?
    )
}
