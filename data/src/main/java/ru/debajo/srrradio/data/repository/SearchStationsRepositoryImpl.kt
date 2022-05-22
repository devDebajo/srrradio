package ru.debajo.srrradio.data.repository

import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal class SearchStationsRepositoryImpl(
    private val serviceHolder: ServiceHolder
) : SearchStationsRepository {

    override suspend fun search(query: String): List<Station> {
        return serviceHolder.createService().search(query).map { station ->
            Station(
                id = station.id,
                name = station.name,
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
