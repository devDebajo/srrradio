package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.preference.UseFavoriteAsDefaultPreference

class UseFavoriteAsDefaultListener(
    private val preference: UseFavoriteAsDefaultPreference,
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands.filterIsInstance<Listen>()
            .flatMapLatest { preference.observe() }
            .map { OnUseFavoriteAsDefaultChanged(it) }
    }

    object Listen : Command

    class OnUseFavoriteAsDefaultChanged(val value: Boolean) : CommandResult
}
