package ru.debajo.srrradio.data.usecase

import android.content.SharedPreferences
import ru.debajo.srrradio.domain.LastStationUseCase
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class LastStationUseCaseImpl(
    sharedPreferences: SharedPreferences,
) : LastStationUseCase {

    override var lastPlaylistId: String? by StringDelegate(sharedPreferences, LAST_PLAYLIST_ID)

    override var lastStationId: String? by StringDelegate(sharedPreferences, LAST_STATION_ID)

    private class StringDelegate(
        private val sharedPreferences: SharedPreferences,
        private val key: String
    ) : ReadWriteProperty<LastStationUseCaseImpl, String?> {

        override fun getValue(thisRef: LastStationUseCaseImpl, property: KProperty<*>): String? {
            return sharedPreferences.getString(key, null)
        }

        override fun setValue(thisRef: LastStationUseCaseImpl, property: KProperty<*>, value: String?) {
            sharedPreferences.edit()
                .putString(key, value)
                .apply()
        }
    }

    private companion object {
        const val LAST_PLAYLIST_ID = "LAST_PLAYLIST_ID"
        const val LAST_STATION_ID = "LAST_STATION_ID"
    }
}
