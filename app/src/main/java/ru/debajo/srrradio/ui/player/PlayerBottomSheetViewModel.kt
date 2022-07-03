package ru.debajo.srrradio.ui.player

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.reduktor.ReduktorViewModel
import ru.debajo.reduktor.reduktorStore
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import timber.log.Timber

@Stable
class PlayerBottomSheetViewModel(
    reduktor: PlayerBottomSheetReduktor,
    commandResultReduktor: PlayerBottomSheetCommandResultReduktor,
    mediaStateListenerCommandProcessor: MediaStateListenerCommandProcessor,
) : ReduktorViewModel<PlayerBottomSheetState, PlayerBottomSheetEvent, PlayerBottomSheetNews>(
    store = reduktorStore(
        initialState = PlayerBottomSheetState(),
        eventReduktor = reduktor,
        commandProcessors = listOf(
            mediaStateListenerCommandProcessor,
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
