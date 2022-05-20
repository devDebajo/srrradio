package ru.debajo.srrradio.common.presentation

typealias Reduktor<State, Event, News> = (state: State, event: Event) -> Akt<State, News>
