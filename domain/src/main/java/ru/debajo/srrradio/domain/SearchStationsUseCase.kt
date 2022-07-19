package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.model.Station

interface SearchStationsUseCase {
    suspend fun search(query: String): List<Station>

    suspend fun searchByUrl(url: String): List<Station>
}
