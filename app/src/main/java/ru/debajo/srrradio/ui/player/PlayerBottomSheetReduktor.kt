package ru.debajo.srrradio.ui.player

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.MediaController
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor

class PlayerBottomSheetReduktor(
    private val mediaController: MediaController,
) : Reduktor<PlayerBottomSheetState, PlayerBottomSheetEvent, PlayerBottomSheetNews> {

    override fun invoke(state: PlayerBottomSheetState, event: PlayerBottomSheetEvent): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        return when (event) {
            PlayerBottomSheetEvent.Start -> reduceStart()
            PlayerBottomSheetEvent.NextStation -> reduceNextStation()
            PlayerBottomSheetEvent.PreviousStation -> reducePreviousStation()
            PlayerBottomSheetEvent.OnPlayPauseClick -> reduceOnPlayPauseClick()
            is PlayerBottomSheetEvent.OnSelectStation -> reduceOnSelectStation(state, event)
        }
    }

    private fun reduceStart(): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        return Akt(
            commands = listOf(MediaStateListenerCommandProcessor.ListenerCommand.Start)
        )
    }

    private fun reduceNextStation(): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        mediaController.next()
        return Akt()
    }

    private fun reducePreviousStation(): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        mediaController.previous()
        return Akt()
    }

    private fun reduceOnPlayPauseClick(): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        mediaController.toggle()
        return Akt()
    }

    private fun reduceOnSelectStation(
        state: PlayerBottomSheetState,
        event: PlayerBottomSheetEvent.OnSelectStation
    ): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        val station = state.stations.getOrNull(event.page) ?: return Akt()
        mediaController.changeStation(station.id, state.playing)
        return Akt()
    }
}