package ru.debajo.srrradio.sync

data class AppGlobalSettings(
    val dynamicIcon: Boolean,
    val themeCode: String,
    val autoSendErrors: Boolean,

    val collection: List<AppGlobalSettingsTrackCollectionItem>,

   // val favoriteStations:
)

data class AppGlobalSettingsTrackCollectionItem(
    val track: String,
    val stationName: String,
)

data class AppGlobalSettingsStation(
    val id: String,

    )