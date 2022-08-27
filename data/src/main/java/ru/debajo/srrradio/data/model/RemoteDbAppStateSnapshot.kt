package ru.debajo.srrradio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
internal data class RemoteDbAppStateSnapshot(
    @SerialName("dynamicIcon")
    val dynamicIcon: Boolean = true,

    @SerialName("themeCode")
    val themeCode: String? = null,

    @SerialName("autoSendErrors")
    val autoSendErrors: Boolean = true,

    @SerialName("collection")
    val collection: List<RemoteDbTrackCollectionItem> = emptyList(),

    @SerialName("favoriteStations")
    val favoriteStations: List<RemoteDbStation> = emptyList(),

    @SerialName("createTimestamp")
    val createTimestamp: String = DateTime(0).toString(),
)
