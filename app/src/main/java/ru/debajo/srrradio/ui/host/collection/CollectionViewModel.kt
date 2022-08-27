package ru.debajo.srrradio.ui.host.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.model.CollectionItem

class CollectionViewModel(
    private val tracksCollectionUseCase: TracksCollectionUseCase
) : ViewModel() {

    private var job: Job? = null
    private val stateMutable: MutableStateFlow<List<UiCollectionItem>> = MutableStateFlow(emptyList())
    val state: StateFlow<List<UiCollectionItem>> = stateMutable.asStateFlow()

    fun load() {
        job?.cancel()
        job = viewModelScope.launch {
            tracksCollectionUseCase.observe().collect {
                stateMutable.value = it.convert()
            }
        }
    }

    fun remove(item: UiCollectionItem) {
        viewModelScope.launch {
            tracksCollectionUseCase.delete(item.track)
        }
    }

    private fun List<CollectionItem>.convert(): List<UiCollectionItem> {
        return map {
            UiCollectionItem(
                track = it.track,
                stationName = it.stationName
            )
        }
    }
}
