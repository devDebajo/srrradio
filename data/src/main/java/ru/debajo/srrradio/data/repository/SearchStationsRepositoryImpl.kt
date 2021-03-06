package ru.debajo.srrradio.data.repository

import ru.debajo.srrradio.data.model.RemoteStation
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal class SearchStationsRepositoryImpl(
    private val serviceHolder: ServiceHolder,
) : SearchStationsRepository {

    override suspend fun search(query: String): List<Station> {
        return serviceHolder.createService().search(query).convert()
    }

    override suspend fun searchByUrl(url: String): List<Station> {
        return serviceHolder.createService().byUrl(url).convert()
    }

    private fun List<RemoteStation>.convert(): List<Station> {
        return map { station ->
            Station(
                id = station.id,
                name = station.name.trim(),
                stream = station.stream.replaceHttp(),
                image = station.image,
            )
        }
    }

    private fun String.replaceHttp(): String {
        return if (startsWith("http://")) {
            replace("http://", "https://")
        } else {
            this
        }
    }
}
