package ru.debajo.srrradio.error

import android.util.Log
import java.io.FileWriter
import org.joda.time.DateTime
import timber.log.Timber

class FileLogTimberTree(
    private val sendErrorsHelper: SendErrorsHelper
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority != Log.ERROR) {
            return
        }

        val now = DateTime.now().toString("MM-dd HH:mm:ss:SSS")
        val currentThreadName = Thread.currentThread().name
        val textToLog = "$now ${tag ?: ""}(${currentThreadName}) : ${message}\n"

        runCatching {
            val file = sendErrorsHelper.getFile()
            FileWriter(file, true).use { it.append(textToLog) }
        }
    }
}
