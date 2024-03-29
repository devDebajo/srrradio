package ru.debajo.srrradio.data.model

import com.google.gson.annotations.SerializedName

internal data class RemoteDbStation(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("stream")
    val stream: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("lat")
    val latitude: Double? = null,

    @SerializedName("lon")
    val longitude: Double? = null,

    @SerializedName("alive")
    val alive: Boolean = true,

    @SerializedName("tags")
    val tags: String = ""
) {
    val tagsList: List<String>
        get() = tags.parseTags()
}
