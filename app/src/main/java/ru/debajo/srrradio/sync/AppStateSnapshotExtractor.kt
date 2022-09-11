package ru.debajo.srrradio.sync

import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.model.AppStateSnapshot
import ru.debajo.srrradio.domain.model.Timestamped
import ru.debajo.srrradio.domain.preference.BasePreference
import ru.debajo.srrradio.error.SendingErrorsManager
import ru.debajo.srrradio.error.SendingErrorsPreference
import ru.debajo.srrradio.icon.DynamicIconPreference
import ru.debajo.srrradio.ui.common.SnowFallPreference
import ru.debajo.srrradio.ui.common.SnowFallUseCase
import ru.debajo.srrradio.ui.theme.SrrradioThemeManager
import ru.debajo.srrradio.ui.theme.SrrradioThemePreference

internal class AppStateSnapshotExtractor(
    private val dynamicIconPreference: DynamicIconPreference,
    private val themeManager: SrrradioThemeManager,
    private val themePreference: SrrradioThemePreference,
    private val sendingErrorsPreference: SendingErrorsPreference,
    private val sendingErrorsManager: SendingErrorsManager,
    private val collectionUseCase: TracksCollectionUseCase,
    private val snowFallUseCase: SnowFallUseCase,
    private val snowFallPreference: SnowFallPreference,
    private val favoriteStationsStateUseCase: FavoriteStationsStateUseCase,
) {
    suspend fun extract(): AppStateSnapshot {
        return AppStateSnapshot(
            dynamicIcon = dynamicIconPreference.toTimestamped(),
            themeCode = themePreference.toTimestamped(),
            autoSendErrors = sendingErrorsPreference.toTimestamped(),
            snowFall = snowFallPreference.toTimestamped(),

            collection = collectionUseCase.get(),
            favoriteStations = favoriteStationsStateUseCase.get(),
        )
    }

    suspend fun applyNewSnapshot(snapshot: AppStateSnapshot) {
        dynamicIconPreference.set(snapshot.dynamicIcon.value)
        val themeCode = snapshot.themeCode.value
        if (!themeCode.isNullOrEmpty()) {
            themeManager.select(themeCode)
        }
        sendingErrorsManager.updateEnabled(snapshot.autoSendErrors.value)
        collectionUseCase.save(snapshot.collection)
        favoriteStationsStateUseCase.save(snapshot.favoriteStations)
        snowFallUseCase.updateEnabled(snapshot.snowFall.value)
    }

    private fun <T> BasePreference<T>.toTimestamped(): Timestamped<T> {
        return Timestamped(
            value = get(),
            timestamp = timestamp
        )
    }
}
