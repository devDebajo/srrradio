package ru.debajo.srrradio.common.presentation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors

@OptIn(FlowPreview::class)
internal class ReduktorStoreImpl<State : Any, Event : Any, News : Any>(
    initialState: State,
    eventReduktor: Reduktor<State, Event, News>,
    commandResultReduktor: Reduktor<State, CommandResult, News> = { _, _ -> Akt() },
    commandProcessors: List<CommandProcessor> = emptyList(),
    initialEvents: List<Event> = emptyList(),
    errorDispatcher: (Throwable) -> Unit = {},
) : ReduktorStore<State, Event, News>, CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main) {

    private val stateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    private val eventsFlow: MutableSharedFlow<Event> = MutableSharedFlow(replay = 1)
    private val newsFlow: MutableSharedFlow<News> = MutableSharedFlow(replay = 1)
    private val commandsFlow: MutableSharedFlow<Command> = MutableSharedFlow(replay = 1)
    private val dispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val errorHandler = CoroutineExceptionHandler { _, exception -> errorDispatcher(exception) }

    override val state: StateFlow<State> = stateFlow.asStateFlow()
    override val news: Flow<News> = newsFlow.asSharedFlow()
    override val currentState: State get() = stateFlow.value

    init {
        launch(dispatcher + errorHandler) {
            eventsFlow
                .map { event -> eventReduktor(stateFlow.value, event) }
                .collect { handleAkt(it) }
        }

        launch(Dispatchers.IO + errorHandler) {
            commandProcessors
                .asFlow()
                .flatMapMerge { it.invoke(commandsFlow) }
                .map { commandResultReduktor(stateFlow.value, it) }
                .collect { handleAkt(it) }
        }

        if (initialEvents.isNotEmpty()) {
            launch(Dispatchers.Default + errorHandler) {
                eventsFlow.emitAll(initialEvents.asFlow())
            }
        }
    }

    override fun onEvent(event: Event) {
        launch {
            eventsFlow.emit(event)
        }
    }

    override fun dispose() {
        cancel()
    }

    private suspend fun handleAkt(pass: Akt<State, News>) = with(pass) {
        if (commands.isNotEmpty()) commandsFlow.emitAll(commands.asFlow())

        if (news.isNotEmpty()) newsFlow.emitAll(news.asFlow())

        if (state != null) {
            val currentState = stateFlow.value
            if (currentState != state) {
                stateFlow.value = state
            }
        }
    }
}

fun <State : Any, Event : Any, News : Any> reduktorStore(
    initialState: State,
    eventReduktor: Reduktor<State, Event, News>,
    commandResultReduktor: Reduktor<State, CommandResult, News> = { _, _ -> Akt() },
    commandProcessors: List<CommandProcessor> = emptyList(),
    initialEvents: List<Event> = emptyList(),
    errorDispatcher: (Throwable) -> Unit = {},
): ReduktorStore<State, Event, News> {
    return ReduktorStoreImpl(
        initialState = initialState,
        eventReduktor = eventReduktor,
        commandResultReduktor = commandResultReduktor,
        commandProcessors = commandProcessors,
        initialEvents = initialEvents,
        errorDispatcher = errorDispatcher,
    )
}
