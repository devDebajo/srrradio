package ru.debajo.srrradio.ui.list

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.reduktor.ReduktorViewModel
import ru.debajo.reduktor.reduktorStore
import ru.debajo.srrradio.ui.list.reduktor.*
import ru.debajo.srrradio.ui.list.reduktor.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.list.reduktor.processor.SearchStationsCommandProcessor
import timber.log.Timber

@Stable
class StationsListViewModel(
    reduktor: StationsListReduktor,
    commandResultReduktor: StationsListCommandResultReduktor,
    searchStationsCommandProcessor: SearchStationsCommandProcessor,
    mediaStateListener: MediaStateListenerCommandProcessor,
    newPlayCommandProcessor: NewPlayCommandProcessor,
) : ReduktorViewModel<StationsListState, StationsListEvent, StationsListNews>(
    store = reduktorStore(
        initialState = StationsListState(),
        eventReduktor = reduktor,
        commandResultReduktor = commandResultReduktor,
        initialEvents = listOf(StationsListEvent.Start, StationsListEvent.OnSearchQueryChanged("synth")),
        commandProcessors = listOf(
            searchStationsCommandProcessor,
            mediaStateListener,
            newPlayCommandProcessor,
        ),
        errorDispatcher = { Timber.e(it) },
    )
) {
    companion object {
        val Local: ProvidableCompositionLocal<StationsListViewModel> = staticCompositionLocalOf { TODO() }
    }
}
