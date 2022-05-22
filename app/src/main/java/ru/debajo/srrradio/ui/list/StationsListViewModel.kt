package ru.debajo.srrradio.ui.list

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.srrradio.common.presentation.ReduktorViewModel
import ru.debajo.srrradio.common.presentation.reduktorStore
import ru.debajo.srrradio.ui.list.reduktor.*
import ru.debajo.srrradio.ui.list.reduktor.processor.PlayerStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import timber.log.Timber

@Stable
class StationsListViewModel(
    reduktor: StationsListReduktor,
    commandResultReduktor: StationsListCommandResultReduktor,
    searchStationsCommandProcessor: SearchStationsCommandProcessor,
    playerStateListenerCommandProcessor: PlayerStateListenerCommandProcessor,
) : ReduktorViewModel<StationsListState, StationsListEvent, StationsListNews>(
    store = reduktorStore(
        initialState = StationsListState.Empty,
        eventReduktor = reduktor,
        commandResultReduktor = commandResultReduktor,
        initialEvents = listOf(StationsListEvent.Start),
        commandProcessors = listOf(
            searchStationsCommandProcessor,
            playerStateListenerCommandProcessor,
        ),
        errorDispatcher = { Timber.e(it) },
    )
) {
    companion object {
        val Local: ProvidableCompositionLocal<StationsListViewModel> = staticCompositionLocalOf { TODO() }
    }
}
