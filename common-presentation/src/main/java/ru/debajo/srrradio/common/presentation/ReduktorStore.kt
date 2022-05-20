package ru.debajo.srrradio.common.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ReduktorStore<State : Any, Event : Any, News : Any> {

    val currentState: State

    val state: StateFlow<State>

    val news: Flow<News>

    fun onEvent(event: Event)

    fun dispose()
}
