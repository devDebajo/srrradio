package ru.debajo.srrradio.error

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers.IO
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

    fun getDate(file: File): LocalDate? {
        val date = file.name
            .replace("srrradio_", "")
            .replace(".log", "")

        return runCatching {
            LocalDate.parse(date, DateTimeFormat.forPattern("dd_MM_yyyy"))
        }.getOrNull()
    }

    suspend fun getFiles(): List<File> {
        return withContext(IO) { getFilesBlocking() }
    }

    suspend fun canSend(): Boolean = getFiles().isNotEmpty()

    suspend fun clearAll() {
        withContext(IO) {
            getFiles().forEach { it.delete() }
        }
    }

    fun openMailApp(context: Context, path: String) {
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", File(path))

        val intent = Intent(Intent.ACTION_SEND)
            .setType("message/rfc822")
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.DEVELOPER_EMAIL))
            .putExtra(Intent.EXTRA_SUBJECT, "Srrradio crash logs")
            .putExtra(Intent.EXTRA_TEXT, dumpDeviceInfo())
            .putExtra(Intent.EXTRA_STREAM, uri)
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(Intent.createChooser(intent, "Send logs"))
    }

    private fun dumpDeviceInfo(): String {
        return StringBuilder()
            .appendLine("App version: ${BuildConfig.VERSION_NAME}")
            .appendLine("OS version: ${Build.VERSION.RELEASE}")
            .appendLine("API level: ${Build.VERSION.SDK_INT}")
            .toString()
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

    private companion object {
        const val FILE_LIMIT = 7
    }
}
