package ru.debajo.srrradio.common.presentation

import kotlinx.coroutines.flow.Flow

interface Command

interface CommandResult {
    companion object {
        val EMPTY = object : CommandResult {}
    }
}

typealias CommandProcessor = (commands: Flow<Command>) -> Flow<CommandResult>
