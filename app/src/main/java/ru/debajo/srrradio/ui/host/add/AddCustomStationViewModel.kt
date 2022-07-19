package ru.debajo.srrradio.ui.host.add

import ru.debajo.reduktor.ReduktorViewModel
import ru.debajo.reduktor.reduktorStore
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationEvent
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationNews
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationState
import ru.debajo.srrradio.ui.processor.SaveCustomStationProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor
import timber.log.Timber

class AddCustomStationViewModel(
    reduktor: AddCustomStationReduktor,
    commandResultReduktor: AddCustomStationCommandResultReduktor,
    searchStationsCommandProcessor: SearchStationsCommandProcessor,
    saveCustomStationProcessor: SaveCustomStationProcessor,
) : ReduktorViewModel<AddCustomStationState, AddCustomStationEvent, AddCustomStationNews>(
    store = reduktorStore(
        initialState = AddCustomStationState(),
        eventReduktor = reduktor,
        commandResultReduktor = commandResultReduktor,
        commandProcessors = listOf(
            searchStationsCommandProcessor,
            saveCustomStationProcessor,
        ),
        errorDispatcher = { Timber.e(it) },
    )
)
