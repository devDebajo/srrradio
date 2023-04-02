package ru.debajo.srrradio.data.usecase

import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.model.Station

class RecommendationsUseCase internal constructor(
    private val serviceHolder: ServiceHolder,
    private val favoriteStationsStateUseCase: FavoriteStationsStateUseCase,
) {
    suspend operator fun invoke(): List<Station> {
        val targetTags = favoriteStationsStateUseCase.get()
            .asSequence()
            .flatMap { it.tags }
            .groupBy { it }
            .asSequence()
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first }
            .toList()

        return serviceHolder.createService().search(
            hideBroken = true,
            tagList = targetTags.joinToString(separator = ","),
            order = "votes",
            reverse = true,
            limit = 10,
        )
            .asSequence()
            .filter { it.health == 1 }
            .map { it.toDomain() }
            .toList()
    }
}
