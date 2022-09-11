package ru.debajo.srrradio.data.model

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

internal data class RemoteDbAppStateSnapshot(
    @SerializedName("dynamicIcon")
    val dynamicIcon: Boolean = true,

    @SerializedName("themeCode")
    val themeCode: String? = null,

    @SerializedName("autoSendErrors")
    val autoSendErrors: Boolean = true,

    @SerializedName("snowFall")
    val snowFall: Boolean = true,

    @SerializedName("collection")
    val collection: List<RemoteDbTrackCollectionItem> = emptyList(),

    @SerializedName("favoriteStations")
    val favoriteStations: List<RemoteDbStation> = emptyList(),

    @SerializedName("createTimestamp")
    val createTimestamp: String = DateTime(0).toString(),
)
