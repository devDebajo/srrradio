package ru.debajo.srrradio.ui.player

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.CommandResult
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.model.MediaState
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.model.UiStationPlayingState

class PlayerBottomSheetCommandResultReduktor : Reduktor<PlayerBottomSheetState, CommandResult, PlayerBottomSheetNews> {

    override fun invoke(state: PlayerBottomSheetState, event: CommandResult): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        return when (event) {
            is MediaStateListenerCommandProcessor.OnNewMediaState -> reduceOnNewMediaState(event)
            else -> Akt()
        }
    }

    private fun reduceOnNewMediaState(
        event: MediaStateListenerCommandProcessor.OnNewMediaState
    ): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        val loadedMediaState: MediaState.Loaded? = event.state as? MediaState.Loaded
        val showBottomSheet = loadedMediaState != null && loadedMediaState.playlist.id != ""
        return Akt(
            state = PlayerBottomSheetState(
                showBottomSheet = showBottomSheet,
                currentStationNameOrEmpty = loadedMediaState?.currentStation?.name.orEmpty(),
                playingState = loadedMediaState?.mediaStationInfo?.playingState ?: UiStationPlayingState.NONE,
                currentStationIndex = loadedMediaState?.currentStationIndex ?: -1,
                stations = loadedMediaState?.playlist?.stations.orEmpty(),
            )
        )
    }
}
