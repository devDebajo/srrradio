package ru.debajo.srrradio.data.usecase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import net.bjoernpetersen.m3u.M3uParser
import net.bjoernpetersen.m3u.model.M3uEntry
import ru.debajo.srrradio.domain.ParseM3uUseCase

internal class ParseM3uUseCaseImpl(
    private val context: Context,
) : ParseM3uUseCase {

    override suspend fun parse(m3uFilePath: String): List<ParseM3uUseCase.ParsedStation> {
        return runCatching {
            parseUnsafe(m3uFilePath).map {
                ParseM3uUseCase.ParsedStation(
                    title = it.title,
                    stream = it.location.url.toString(),
                    poster = it.metadata.logo,
                )
            }
        }.getOrElse { emptyList() }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun parseUnsafe(path: String): List<M3uEntry> {
        return withContext(IO) {
            val stream = context.contentResolver.openInputStream(Uri.parse(path))?.reader()
            if (stream != null) {
                val fileEntries = M3uParser.parse(stream)
                M3uParser.resolveNestedPlaylists(fileEntries)
            } else {
                emptyList()
            }
        }
    }
}
