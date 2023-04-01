package ru.debajo.srrradio.ui.host.add

import androidx.compose.ui.text.input.TextFieldValue
import ru.debajo.reduktor.Akt
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.Reduktor
import ru.debajo.srrradio.ui.ext.Empty
import ru.debajo.srrradio.ui.ext.isNotEmpty
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationEvent
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationNews
import ru.debajo.srrradio.ui.host.add.model.AddCustomStationState
import ru.debajo.srrradio.ui.processor.SaveCustomStationProcessor
import ru.debajo.srrradio.ui.processor.SearchStationsCommandProcessor

class AddCustomStationReduktor : Reduktor<AddCustomStationState, AddCustomStationEvent, AddCustomStationNews> {

    override fun invoke(state: AddCustomStationState, event: AddCustomStationEvent): Akt<AddCustomStationState, AddCustomStationNews> {
        return when (event) {
            is AddCustomStationEvent.OnStreamChanged -> reduceOnStreamChanged(state, event)
            is AddCustomStationEvent.OnNameChanged -> reduceOnNameChanged(state, event)
            is AddCustomStationEvent.Save -> reduceSave(state)
        }
    }

    private fun reduceOnStreamChanged(
        state: AddCustomStationState,
        event: AddCustomStationEvent.OnStreamChanged
    ): Akt<AddCustomStationState, AddCustomStationNews> {
        val commands = mutableListOf<Command>()
        var newState = state.copy(stream = event.stream, name = TextFieldValue.Empty)
        if (event.stream.isNotEmpty()) {
            newState = newState.copy(searching = true)
            commands.add(SearchStationsCommandProcessor.Action.SearchByUrl(event.stream.text.trim()))
        }
        return Akt(state = newState, commands = commands)
    }

    private fun reduceOnNameChanged(
        state: AddCustomStationState,
        event: AddCustomStationEvent.OnNameChanged
    ): Akt<AddCustomStationState, AddCustomStationNews> {
        return Akt(
            state = state.copy(name = event.name, searching = false),
            commands = listOf(SearchStationsCommandProcessor.Action.Cancel)
        )
    }

    private fun reduceSave(
        state: AddCustomStationState
    ): Akt<AddCustomStationState, AddCustomStationNews> {
        if (!state.canSave) {
            return Akt()
        }

        return Akt(commands = listOf(SaveCustomStationProcessor.Save(state.stream.text.trim(), state.name.text.trim())))
    }
}
