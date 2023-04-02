package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.model.Station

class RecommendationsUseCase internal constructor(
    private val serviceHolder: ServiceHolder,
    private val favoriteStationsStateUseCase: FavoriteStationsStateUseCase,
) {
    suspend operator fun invoke(limit: Int): List<Station> {
        val favoriteStations = favoriteStationsStateUseCase.get()
        if (favoriteStations.isEmpty()) {
            return emptyList()
        }
        val targetTags = favoriteStations
            .asSequence()
            .flatMap { it.tags }
            .groupBy { it }
            .asSequence()
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }
            .take(1)
            .map { it.first }
            .toList()
        val favoriteIds = favoriteStations.map { it.id }.toSet()
        val remoteStations = serviceHolder.createService().search(
            hideBroken = true,
            tagList = targetTags.joinToString(separator = ","),
            order = "votes",
            reverse = true,
            limit = 100,
        )
        return remoteStations
            .asSequence()
            .filter { it.health == 1 && it.id !in favoriteIds }
            .take(limit)
            .map { it.toDomain() }
            .toList()
    }
}
