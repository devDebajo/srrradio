package ru.debajo.srrradio.ui.processor.interactor

import android.net.Uri
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.model.Station

class LoadM3uInteractor(
    private val parseM3uUseCase: ParseM3uUseCase,
    private val searchStationsUseCase: SearchStationsUseCase,
    private val updateFavoriteStationStateUseCase: UpdateFavoriteStationStateUseCase,
    private val userStationUseCase: UserStationUseCase,
) {

    private val ParseM3uUseCase.ParsedStation.isStream: Boolean
        get() = Uri.parse(this.stream).scheme in listOf("http", "https")

    suspend fun load(m3uFilePath: String) {
        runCatchingNonCancellation { loadUnsafe(m3uFilePath) }
    }

    private suspend fun loadUnsafe(m3uFilePath: String) {
        parseM3uUseCase.parse(m3uFilePath)
            .filter { it.isStream }
            .map { resolve(it) }
            .onEach {
                userStationUseCase.persist(it)
                updateFavoriteStationStateUseCase.update(it, inFavorite = true)
            }
    }

    private suspend fun resolve(parsedStation: ParseM3uUseCase.ParsedStation): Station {
        val persistedStation = userStationUseCase.getByStream(parsedStation.stream)
        if (persistedStation != null) {
            return merge(persistedStation, parsedStation)
        }
        val foundStation = searchStationsUseCase.searchByUrl(parsedStation.stream).firstOrNull()
        if (foundStation != null) {
            return merge(foundStation, parsedStation)
        }
        return userStationUseCase.create(
            stream = parsedStation.stream,
            name = parsedStation.title ?: parsedStation.stream,
            poster = parsedStation.poster
        )
    }

    private fun merge(station: Station, parsedStation: ParseM3uUseCase.ParsedStation): Station {
        return Station(
            id = station.id,
            name = parsedStation.title ?: station.name,
            stream = parsedStation.stream,
            image = parsedStation.poster ?: station.image,
            location = station.location,
            alive = station.alive,
            tags = station.tags,
        )
    }
}
