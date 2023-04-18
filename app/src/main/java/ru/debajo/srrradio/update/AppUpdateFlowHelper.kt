package ru.debajo.srrradio.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.download.library.DownloadImpl
import com.download.library.DownloadListenerAdapter
import com.download.library.Extra
import java.io.File
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.debajo.srrradio.BuildConfig
import ru.debajo.srrradio.auth.F
import ru.debajo.srrradio.auth.log
import ru.debajo.srrradio.common.GooglePlayInAppUpdateHelper
import ru.debajo.srrradio.common.utils.toTimber
import ru.debajo.srrradio.domain.repository.ConfigRepository

class AppUpdateFlowHelper(
    private val context: Context,
    private val configRepository: ConfigRepository,
    private val googlePlayInAppUpdateHelper: GooglePlayInAppUpdateHelper,
    private val f: F,
) {
    fun updateAppAsFlow(): Flow<UpdateProgress> {
        return callbackFlow {
            val loaded = updateApp { progress ->
                trySend(UpdateProgress.Loading(progress))
            }

            if (loaded) {
                trySend(UpdateProgress.Loaded)
            } else {
                trySend(UpdateProgress.Failed)
            }

            channel.close()
        }
    }

    suspend fun updateApp(onProgress: (Float) -> Unit): Boolean {
        if (googlePlayInAppUpdateHelper.installedFromGooglePlay()) {
            f.log("start_update_by_google_play")
            googlePlayInAppUpdateHelper.update()
            return true
        }

        f.log("start_embedded_update")
        val updateFileUrl = configRepository.provide().updateFileUrl ?: return false
        val uri = download(
            url = updateFileUrl,
            targetFile = File(context.cacheDir, "app-update.apk"),
            onProgress = onProgress,
        ) ?: return false
        val downloadedUri = prepareUriForIntent(uri) ?: return false
        val intent = buildIntent(downloadedUri)
        return runCatching { context.startActivity(intent) }.map { true }.getOrElse { false }
    }

    private suspend fun download(
        url: String,
        targetFile: File,
        onProgress: (Float) -> Unit
    ): Uri? {
        if (targetFile.exists()) {
            targetFile.delete()
        }

        val downloader = DownloadImpl.getInstance(context)
        val task = downloader
            .url(url)
            .target(targetFile)
            .setForceDownload(true)

        return suspendCancellableCoroutine { continuation ->
            task.enqueue(object : DownloadListenerAdapter() {
                override fun onProgress(url: String?, downloaded: Long, length: Long, usedTime: Long) {
                    onProgress((downloaded / length.toDouble()).toFloat().coerceIn(0f, 1f))
                }

                override fun onResult(throwable: Throwable?, path: Uri?, url: String?, extra: Extra?): Boolean {
                    if (path != null) {
                        continuation.resume(path)
                    } else {
                        continuation.resume(null)
                    }
                    return false
                }
            })

            continuation.invokeOnCancellation { downloader.cancel(url) }
        }
    }

    private fun prepareUriForIntent(uri: Uri): Uri? {
        return runCatching {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                File(uri.path!!)
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

    sealed interface UpdateProgress {
        class Loading(val progress: Float) : UpdateProgress

        object Loaded : UpdateProgress

        object Failed : UpdateProgress
    }
}
