package ru.debajo.srrradio.domain.model

data class Station(
    val id: String,
    val name: String,
    val stream: String,
    val image: String?,
    val location: LatLng?,
    val alive: Boolean,
    val tags: List<String>,
) {
    val joinedTags: String
        get() = tags.joinToString(separator = ",")
}
