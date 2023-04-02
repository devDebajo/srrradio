package ru.debajo.srrradio.domain.model

data class AppStateSnapshot(
    val themeCode: Timestamped<String?>,
    val autoSendErrors: Timestamped<Boolean>,
    val snowFall: Timestamped<Boolean>,

    val collection: List<CollectionItem>,
    val favoriteStations: List<Station>,
)
