package ru.debajo.srrradio.ui.host.main.player

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.reduktor.ReduktorViewModel
import ru.debajo.reduktor.reduktorStore
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetEvent
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetNews
import ru.debajo.srrradio.ui.host.main.player.model.PlayerBottomSheetState
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.player.reduktor.PlayerBottomSheetReduktor
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.SleepTimerListenerProcessor
import timber.log.Timber

@Stable
class PlayerBottomSheetViewModel(
    reduktor: PlayerBottomSheetReduktor,
    commandResultReduktor: PlayerBottomSheetCommandResultReduktor,
    mediaStateListenerCommandProcessor: MediaStateListenerCommandProcessor,
    addFavoriteStationProcessor: AddFavoriteStationProcessor,
    listenFavoriteStationsProcessor: ListenFavoriteStationsProcessor,
    sleepTimerListenerProcessor: SleepTimerListenerProcessor,
) : ReduktorViewModel<PlayerBottomSheetState, PlayerBottomSheetEvent, PlayerBottomSheetNews>(
    store = reduktorStore(
        initialState = PlayerBottomSheetState(),
        eventReduktor = reduktor,
        commandProcessors = listOf(
            mediaStateListenerCommandProcessor,
            addFavoriteStationProcessor,
            listenFavoriteStationsProcessor,
            sleepTimerListenerProcessor,
        ),
        commandResultReduktor = commandResultReduktor,
        initialEvents = listOf(PlayerBottomSheetEvent.Start),
        errorDispatcher = { Timber.e(it) },
    )
) {
    companion object {
        val Local: ProvidableCompositionLocal<PlayerBottomSheetViewModel> = staticCompositionLocalOf { TODO() }
    }
}
