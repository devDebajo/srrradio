package ru.debajo.srrradio.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val stations: List<Station>,
)
