package ru.debajo.srrradio.error

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.WorkerThread
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import ru.debajo.srrradio.BuildConfig

class SendErrorsHelper(private val context: Context) {

    @WorkerThread
    fun getFile(): File {
        val now = LocalDate.now().toString("dd_MM_yyyy")
        val fileName = "srrradio_${now}.log"
        val file = File(getDirectory(), fileName)
        if (!file.exists()) {
            file.createNewFile()
            removeOldestFiles()
        }
        return file
    }

    private fun getDate(file: File): LocalDate? {
        val date = file.name
            .replace("srrradio_", "")
            .replace(".log", "")

        return runCatching {
            LocalDate.parse(date, DateTimeFormat.forPattern("dd_MM_yyyy"))
        }.getOrNull()
    }

    suspend fun getFiles(): List<File> {
        return withContext(Dispatchers.IO) { getFilesBlocking() }
    }

    suspend fun canSend(): Boolean {
        val canSendEmail = canSendEmail()
        val hasFiles = getFiles().isNotEmpty()
        return canSendEmail && hasFiles
    }

    @WorkerThread
    private fun removeOldestFiles() {
        val files = getFilesBlocking()
        if (files.size <= FILE_LIMIT) {
            return
        }

        files
            .map { it to getDate(it) }
            .filter { it.second != null }
            .sortedBy { it.second!! }
            .take(files.size - FILE_LIMIT)
            .forEach { it.first.delete() }
    }

    @WorkerThread
    private fun getFilesBlocking(): List<File> {
        val dir = getDirectory()
        val files = dir.listFiles().orEmpty().toList()
        return files.filter { it.isFile && it.extension == "log" }
    }

    @WorkerThread
    private fun getDirectory(): File {
        val directory = File(context.cacheDir, "logs")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    private fun canSendEmail(): Boolean = canLaunchIntent(buildIntentRaw())

    private fun buildIntentRaw(): Intent {
        return Intent(Intent.ACTION_SENDTO)
            .setData(Uri.parse("mailto:${BuildConfig.DEVELOPER_EMAIL}"))
    }

    private fun canLaunchIntent(intent: Intent): Boolean {
        return runCatching { intent.resolveActivity(context.packageManager) != null }.getOrElse { false }
    }

    private companion object {
        const val FILE_LIMIT = 7
    }
}
