package ru.debajo.srrradio.domain

interface ParseM3uUseCase {
    suspend fun parse(m3uFilePath: String): List<ParsedStation>

    data class ParsedStation(
        val title: String?,
        val stream: String,
        val poster: String?
    )
}
