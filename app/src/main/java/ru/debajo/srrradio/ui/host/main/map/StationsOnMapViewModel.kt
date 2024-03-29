package ru.debajo.srrradio.ui.host.main.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.debajo.srrradio.R
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UserLocationUseCase
import ru.debajo.srrradio.domain.model.LatLng
import ru.debajo.srrradio.media.MediaController
import ru.debajo.srrradio.ui.model.UiPlaylist
import ru.debajo.srrradio.ui.model.UiStation
import ru.debajo.srrradio.ui.model.toUi

class StationsOnMapViewModel(
    private val context: Context,
    private val useCase: SearchStationsUseCase,
    private val mediaController: MediaController,
    private val locationUseCase: UserLocationUseCase,
) : ViewModel() {

    private val _stations: MutableStateFlow<List<UiStation>> = MutableStateFlow(emptyList())
    private val _moveToLocationRequest: MutableSharedFlow<MoveToLocationRequest> = MutableSharedFlow()

    val stations: StateFlow<List<UiStation>> = _stations.asStateFlow()
    val moveToLocationRequest: Flow<MoveToLocationRequest> = _moveToLocationRequest.asSharedFlow()

    fun load() {
        viewModelScope.launch {
            val stations = useCase.getAllStationsForMap().map { it.toUi() }
            _stations.value = stations
        }
    }

    fun startPlaying(station: UiStation) {
        viewModelScope.launch {
            mediaController.newPlay(
                playlist = UiPlaylist(
                    id = station.id,
                    name = context.getString(R.string.playlist_on_map_format, station.name),
                    stations = listOf(station)
                ),
                stationId = station.id,
                play = true,
            )
        }
    }

    fun loadCurrentLocation() {
        viewModelScope.launch {
            _moveToLocationRequest.subscriptionCount.first { it > 0 }
            val lastCachedLocation = locationUseCase.getLastCachedLocation()
            if (lastCachedLocation != null) {
                _moveToLocationRequest.emit(
                    MoveToLocationRequest(
                        location = lastCachedLocation,
                        animated = false
                    )
                )
            }

            val location = locationUseCase.getCurrentLocation()
            if (location != null) {
                _moveToLocationRequest.emit(
                    MoveToLocationRequest(
                        location = location,
                        animated = true
                    )
                )
            }
        }
    }

    class MoveToLocationRequest(
        val location: LatLng,
        val animated: Boolean,
    )
}
