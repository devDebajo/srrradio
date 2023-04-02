package ru.debajo.srrradio.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.media.model.MediaState
import ru.debajo.srrradio.ui.model.UiStationPlayingState

class PlayerWidgetManager(
    private val context: Context,
    private val mediaController: MediaController,
) {

    private val manager: GlanceAppWidgetManager = GlanceAppWidgetManager(context)

    suspend fun listen() {
        mediaController.state.collect {
            when (it) {
                MediaState.Empty,
                MediaState.Loading,
                MediaState.None -> {
                    updateAllWidgets {
                        this[PREFERENCES_KEY_NO_DATA] = true
                    }
                }
                is MediaState.Loaded -> applyNewState(it)
            }
        }
    }

    private suspend fun applyNewState(currentState: MediaState.Loaded) {
        updateAllWidgets {
            this[PREFERENCES_KEY_NO_DATA] = false
            this[PREFERENCES_KEY_HAS_PREVIOUS_STATION] = currentState.hasPreviousStation
            this[PREFERENCES_KEY_HAS_NEXT_STATION] = currentState.hasNextStation
            this[PREFERENCES_KEY_IS_PLAYING] = currentState.playing
            this[PREFERENCES_KEY_IS_BUFFERING] = currentState.buffering
            this[PREFERENCES_KEY_STATION_NAME] = currentState.currentStation?.name.orEmpty()
            val title = currentState.mediaStationInfo?.title
            if (title.isNullOrEmpty()) {
                this.remove(PREFERENCES_KEY_TRACK_NAME)
            } else {
                this[PREFERENCES_KEY_TRACK_NAME] = title
            }
        }
    }

    private suspend fun updateAllWidgets(block: MutablePreferences.() -> Unit) {
        val ids = manager.getGlanceIds(PlayerWidget::class.java)
        for (id in ids) {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) {
                it.toMutablePreferences().apply(block)
            }
            PlayerWidget().update(context, id)
        }
    }


    sealed interface WidgetState {
        object NoData : WidgetState

        data class HasData(
            val hasPreviousStation: Boolean,
            val hasNextStation: Boolean,
            val playingState: UiStationPlayingState,
            val stationName: String,
            val playingTitle: String?,
        ) : WidgetState
    }

    companion object {
        private val PREFERENCES_KEY_NO_DATA = booleanPreferencesKey("no_data")
        private val PREFERENCES_KEY_HAS_PREVIOUS_STATION = booleanPreferencesKey("has_prev")
        private val PREFERENCES_KEY_HAS_NEXT_STATION = booleanPreferencesKey("has_next")
        private val PREFERENCES_KEY_IS_PLAYING = booleanPreferencesKey("is_playing")
        private val PREFERENCES_KEY_IS_BUFFERING = booleanPreferencesKey("is_buffering")
        private val PREFERENCES_KEY_STATION_NAME = stringPreferencesKey("station_name")
        private val PREFERENCES_KEY_TRACK_NAME = stringPreferencesKey("track_name")

        fun extractState(preferences: Preferences): WidgetState {
            if (preferences[PREFERENCES_KEY_NO_DATA] == true) {
                return WidgetState.NoData
            }
            val playing = preferences[PREFERENCES_KEY_IS_PLAYING] == true
            val buffering = preferences[PREFERENCES_KEY_IS_BUFFERING] == true
            val playingState = when {
                buffering -> UiStationPlayingState.BUFFERING
                playing -> UiStationPlayingState.PLAYING
                else -> UiStationPlayingState.NONE
            }
            return WidgetState.HasData(
                hasPreviousStation = preferences[PREFERENCES_KEY_HAS_PREVIOUS_STATION] == true,
                hasNextStation = preferences[PREFERENCES_KEY_HAS_NEXT_STATION] == true,
                playingState = playingState,
                stationName = preferences[PREFERENCES_KEY_STATION_NAME].orEmpty(),
                playingTitle = preferences[PREFERENCES_KEY_TRACK_NAME],
            )
        }
    }
}
