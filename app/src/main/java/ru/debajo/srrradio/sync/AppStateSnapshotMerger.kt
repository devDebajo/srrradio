package ru.debajo.srrradio.sync

import ru.debajo.srrradio.domain.model.AppStateSnapshot
import ru.debajo.srrradio.domain.model.Timestamped

internal class AppStateSnapshotMerger {
    fun merge(local: AppStateSnapshot, forSync: AppStateSnapshot): AppStateSnapshot {
        return AppStateSnapshot(
            themeCode = local.themeCode mergeWith forSync.themeCode,
            autoSendErrors = local.autoSendErrors mergeWith forSync.autoSendErrors,
            snowFall = local.snowFall mergeWith forSync.snowFall,

            collection = (local.collection + forSync.collection).distinct(),
            favoriteStations = (local.favoriteStations + forSync.favoriteStations).distinctBy { it.id },
        )
    }

    private infix fun <T> Timestamped<T>.mergeWith(other: Timestamped<T>): Timestamped<T> {
        return if (timestamp > other.timestamp) this else other
    }
}
