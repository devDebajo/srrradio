package ru.debajo.srrradio.common.presentation

data class Akt<State : Any, News : Any>(
    val state: State? = null,
    val commands: List<Command> = emptyList(),
    val news: List<News> = emptyList(),
)
