package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.model.Station

interface SearchStationsUseCase {
    suspend fun search(query: String): List<Station>

    suspend fun searchByUrl(url: String): List<Station>

    suspend fun searchByLocation(latitude: Double, longitude: Double, radiusInMeters: Float): List<Station>

    suspend fun searchNew(limit: Int): List<Station>

    suspend fun searchPopular(limit: Int): List<Station>
}
