package ru.debajo.srrradio.data.repository

import android.location.Location
import ru.debajo.srrradio.data.model.RemoteStation
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal class SearchStationsRepositoryImpl(
    private val serviceHolder: ServiceHolder,
) : SearchStationsRepository {

    override suspend fun search(query: String): List<Station> {
        return serviceHolder.createService().search(query = query.trim(), hasGeoInfo = null)
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

    override suspend fun searchByLocation(latitude: Double, longitude: Double, radiusInMeters: Float): List<Station> {
        return serviceHolder.createService().search(query = null, hasGeoInfo = true)
            .asSequence()
            .filterAlive()
            .filter { it.distanceTo(latitude, longitude) <= radiusInMeters }
            .convert()
            .toList()
    }

    override suspend fun searchNew(limit: Int): List<Station> {
        return serviceHolder.createService()
            .newStations(limit = limit, hideBroken = true)
            .asSequence()
            .convert()
            .toList()
    }

    override suspend fun searchPopular(limit: Int): List<Station> {
        return serviceHolder.createService()
            .popularStations(limit = limit, hideBroken = true)
            .asSequence()
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

    private val distanceResults: ThreadLocal<FloatArray> = object : ThreadLocal<FloatArray>() {
        override fun initialValue(): FloatArray = floatArrayOf(0f)
    }

    private fun RemoteStation.distanceTo(latitude: Double, longitude: Double): Float {
        if (this.latitude == null || this.longitude == null) {
            return Float.MAX_VALUE
        }
        val distanceResults = distanceResults.get()!!
        Location.distanceBetween(
            this.latitude,
            this.longitude,
            latitude,
            longitude,
            distanceResults
        )

        return distanceResults[0]
    }
}
