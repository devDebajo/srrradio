package ru.debajo.srrradio.ui.processor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.debajo.reduktor.Command
import ru.debajo.reduktor.CommandProcessor
import ru.debajo.reduktor.CommandResult
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.model.CollectionItem

class TrackCollectionListener(
    private val tracksCollectionUseCase: TracksCollectionUseCase
) : CommandProcessor {

    override fun invoke(commands: Flow<Command>): Flow<CommandResult> {
        return commands
            .filterIsInstance<Listen>()
            .flatMapLatest {
                tracksCollectionUseCase.observe().map {
                    TrackCollectionChanged(it)
                }
            }
    }

    object Listen : Command

    class TrackCollectionChanged(val collection: List<CollectionItem>) : CommandResult
}
