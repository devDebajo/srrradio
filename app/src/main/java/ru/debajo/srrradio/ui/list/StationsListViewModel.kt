package ru.debajo.srrradio.ui.list

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.reduktor.ReduktorViewModel
import ru.debajo.reduktor.reduktorStore
import ru.debajo.srrradio.ui.list.model.StationsListEvent
import ru.debajo.srrradio.ui.list.model.StationsListNews
import ru.debajo.srrradio.ui.list.model.StationsListState
import ru.debajo.srrradio.ui.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import timber.log.Timber

@Stable
class StationsListViewModel(
    reduktor: StationsListReduktor,
    commandResultReduktor: StationsListCommandResultReduktor,
    searchStationsCommandProcessor: SearchStationsCommandProcessor,
    mediaStateListener: MediaStateListenerCommandProcessor,
    newPlayCommandProcessor: NewPlayCommandProcessor,
    listenFavoriteStationsProcessor: ListenFavoriteStationsProcessor,
) : ReduktorViewModel<StationsListState, StationsListEvent, StationsListNews>(
    store = reduktorStore(
        initialState = StationsListState(),
        eventReduktor = reduktor,
        commandResultReduktor = commandResultReduktor,
        initialEvents = listOf(StationsListEvent.Start),
        commandProcessors = listOf(
            searchStationsCommandProcessor,
            mediaStateListener,
            newPlayCommandProcessor,
            listenFavoriteStationsProcessor,
        ),
        errorDispatcher = { Timber.e(it) },
    )
) {
    companion object {
        val Local: ProvidableCompositionLocal<StationsListViewModel> = staticCompositionLocalOf { TODO() }
    }
}
