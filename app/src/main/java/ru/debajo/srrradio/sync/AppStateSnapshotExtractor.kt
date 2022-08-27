package ru.debajo.srrradio.sync

import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.model.AppStateSnapshot
import ru.debajo.srrradio.domain.model.Timestamped
import ru.debajo.srrradio.domain.preference.BasePreference
import ru.debajo.srrradio.error.SendingErrorsPreference
import ru.debajo.srrradio.icon.DynamicIconPreference
import ru.debajo.srrradio.ui.theme.SrrradioThemePreference

internal class AppStateSnapshotExtractor(
    private val dynamicIconPreference: DynamicIconPreference,
    private val themePreference: SrrradioThemePreference,
    private val sendingErrorsPreference: SendingErrorsPreference,
    private val collectionUseCase: TracksCollectionUseCase,
    private val favoriteStationsStateUseCase: FavoriteStationsStateUseCase,
) {
    suspend fun extract(): AppStateSnapshot {
        return AppStateSnapshot(
            dynamicIcon = dynamicIconPreference.toTimestamped(),
            themeCode = themePreference.toTimestamped(),
            autoSendErrors = sendingErrorsPreference.toTimestamped(),

            collection = collectionUseCase.get(),
            favoriteStations = favoriteStationsStateUseCase.get(),
        )
    }

    private fun <T> BasePreference<T>.toTimestamped(): Timestamped<T> {
        return Timestamped(
            value = get(),
            timestamp = timestamp
        )
    }
}
