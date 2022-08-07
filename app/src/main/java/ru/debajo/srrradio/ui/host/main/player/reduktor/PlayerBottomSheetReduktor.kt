package ru.debajo.srrradio.ui.host.main.player.reduktor

import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetEvent
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetNews
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetState
import ru.debajo.srrradio.ui.model.toDomain
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.AddTrackToCollectionProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.SleepTimerListenerProcessor

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
            is PlayerBottomSheetEvent.UpdateStationFavorite -> reduceUpdateStationFavorite(state, event)
            is PlayerBottomSheetEvent.AddTrackToCollection -> reduceAddTrackToCollection(state, event)
        }
    }

    private fun reduceAddTrackToCollection(
        state: PlayerBottomSheetState,
        event: PlayerBottomSheetEvent.AddTrackToCollection
    ): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        val station = state.stations.getOrNull(state.currentStationIndex) ?: return Akt()
        return Akt(
            commands = listOf(AddTrackToCollectionProcessor.Save(event.title, station))
        )
    }

    private fun reduceStart(): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        return Akt(
            commands = listOf(
                MediaStateListenerCommandProcessor.ListenerCommand.Start,
                ListenFavoriteStationsProcessor.Listen,
                SleepTimerListenerProcessor.Start,
            )
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
        val currentStation = state.stations.getOrNull(state.currentStationIndex)
        if (currentStation?.id == station.id) {
            return Akt()
        }
        mediaController.changeStation(station.id, state.playing)
        return Akt()
    }

    private fun reduceUpdateStationFavorite(
        state: PlayerBottomSheetState,
        event: PlayerBottomSheetEvent.UpdateStationFavorite
    ): Akt<PlayerBottomSheetState, PlayerBottomSheetNews> {
        val station = state.stations.getOrNull(state.currentStationIndex) ?: return Akt()
        return Akt(
            commands = listOf(AddFavoriteStationProcessor.Update(station.toDomain(), event.favorite))
        )
    }
}