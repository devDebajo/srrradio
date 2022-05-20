package ru.debajo.srrradio.common.presentation

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel

open class ReduktorViewModel<State : Any, Event : Any, News : Any>(
    private val store: ReduktorStore<State, Event, News>,
) : ViewModel(), ReduktorStore<State, Event, News> by store {

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}

fun <State : Any, Event : Any, News : Any> reduktorViewModel(
    initialState: State,
    eventReduktor: Reduktor<State, Event, News>,
    commandResultReduktor: Reduktor<State, CommandResult, News> = { _, _ -> Akt() },
    commandProcessors: List<CommandProcessor> = emptyList(),
    initialEvents: List<Event> = emptyList(),
    errorDispatcher: (Throwable) -> Unit = {},
): ReduktorViewModel<State, Event, News> {
    return ReduktorViewModel(
        store = reduktorStore(
            initialState = initialState,
            eventReduktor = eventReduktor,
            commandResultReduktor = commandResultReduktor,
            commandProcessors = commandProcessors,
            initialEvents = initialEvents,
            errorDispatcher = errorDispatcher,
        )
    )
}
