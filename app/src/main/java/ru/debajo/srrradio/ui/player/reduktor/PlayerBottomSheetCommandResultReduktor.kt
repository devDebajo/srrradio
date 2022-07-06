package ru.debajo.srrradio.ui.player.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.model.MediaState
import ru.debajo.srrradio.ui.model.UiStationPlayingState
import ru.debajo.srrradio.ui.player.model.PlayerBottomSheetNews
import ru.debajo.srrradio.ui.player.model.PlayerBottomSheetState
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor

class PlayerBottomSheetCommandResultReduktor : Reduktor<PlayerBottomSheetState, CommandResult, PlayerBottomSheetNews> {

    override fun invoke(state: PlayerBottomSheetState, event: CommandResult): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        return when (event) {
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(state, event)
            is ListenFavoriteStationsProcessor.Result -> reduceNewFavoriteStations(state, event)
            else -> Akt()
        }
    }

    private fun reduceOnNewMediaState(
        state: PlayerBottomSheetState,
        event: MediaStateListenerCommandProcessor.OnNewMediaState
    ): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        val loadedMediaState: MediaState.Loaded? = event.state as? MediaState.Loaded
        val showBottomSheet = loadedMediaState != null && loadedMediaState.playlist.id != ""
        return Akt(
            state = state.copy(
                showBottomSheet = showBottomSheet,
                currentStationNameOrEmpty = loadedMediaState?.currentStation?.name.orEmpty(),
                playingState = loadedMediaState?.mediaStationInfo?.playingState ?: UiStationPlayingState.NONE,
                currentStationIndex = loadedMediaState?.currentStationIndex ?: -1,
                stations = loadedMediaState?.playlist?.stations.orEmpty(),
            )
        )
    }

    private fun reduceNewFavoriteStations(
        state: PlayerBottomSheetState,
        event: ListenFavoriteStationsProcessor.Result
    ): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        return Akt(state.copy(favoriteStationsIds = event.stations.map { it.id }.toSet()))
    }
}
