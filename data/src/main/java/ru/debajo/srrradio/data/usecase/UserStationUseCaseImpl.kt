package ru.debajo.srrradio.data.usecase

import java.util.UUID
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.model.DbStation
import ru.debajo.srrradio.data.model.toDomain
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.model.Station

internal class UserStationUseCaseImpl(
    private val stationDao: DbStationDao,
) : UserStationUseCase {

    override suspend fun create(stream: String, name: String): Station {
        val dbStation = DbStation(
            id = UUID.randomUUID().toString(),
            stream = stream,
            name = name,
            image = null
        )

        stationDao.insert(dbStation)
        return dbStation.toDomain()
    }
}
