package ru.debajo.srrradio.data.repository

import ru.debajo.srrradio.data.model.RemoteStation
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal class SearchStationsRepositoryImpl(
    private val serviceHolder: ServiceHolder,
) : SearchStationsRepository {

    override suspend fun search(query: String): List<Station> {
        return serviceHolder.createService().search(query.trim())
            .asSequence()
            .filterAlive()
            .convert()
            .toList()
    }

    override suspend fun searchByUrl(url: String): List<Station> {
        return serviceHolder.createService().byUrl(url.trim())
            .asSequence()
            .filterAlive()
            .convert()
            .toList()
    }

    private fun Sequence<RemoteStation>.filterAlive(): Sequence<RemoteStation> {
        return filter { it.health == 1 }
    }

    private fun Sequence<RemoteStation>.convert(): Sequence<Station> {
        return map { station ->
            Station(
                id = station.id,
                name = station.name.trim(),
                stream = station.stream,
                image = station.image,
            )
        }
    }
}
