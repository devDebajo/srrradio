package ru.debajo.srrradio.ui.host.main.list

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.reduktor.ReduktorViewModel
import ru.debajo.reduktor.reduktorStore
import ru.debajo.srrradio.ui.host.main.list.model.StationsListEvent
import ru.debajo.srrradio.ui.host.main.list.model.StationsListNews
import ru.debajo.srrradio.ui.host.main.list.model.StationsListState
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListCommandResultReduktor
import ru.debajo.srrradio.ui.host.main.list.reduktor.StationsListReduktor
import ru.debajo.srrradio.ui.processor.AddFavoriteStationProcessor
import ru.debajo.srrradio.ui.processor.ListenFavoriteStationsProcessor
import ru.debajo.srrradio.ui.processor.MediaStateListenerCommandProcessor
import ru.debajo.srrradio.ui.processor.NewPlayCommandProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import ru.debajo.srrradio.ui.processor.TrackCollectionListener
import timber.log.Timber

@Stable
class StationsListViewModel(
    reduktor: StationsListReduktor,
    commandResultReduktor: StationsListCommandResultReduktor,
    searchStationsCommandProcessor: SearchStationsCommandProcessor,
    mediaStateListener: MediaStateListenerCommandProcessor,
    newPlayCommandProcessor: NewPlayCommandProcessor,
    listenFavoriteStationsProcessor: ListenFavoriteStationsProcessor,
    addFavoriteStationProcessor: AddFavoriteStationProcessor,
    trackCollectionListener: TrackCollectionListener,
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
            addFavoriteStationProcessor,
            trackCollectionListener,
        ),
        errorDispatcher = { Timber.e(it) },
    )
) {
    companion object {
        val Local: ProvidableCompositionLocal<StationsListViewModel> = staticCompositionLocalOf { TODO() }
    }
}
