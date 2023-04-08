package ru.debajo.srrradio.data.appversion

import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase

class AppVersionMigrationTo10(
    private val favoriteStationsStateUseCase: FavoriteStationsStateUseCase,
) : AppVersionMigration(10) {

    override val allowFail: Boolean = true

    override suspend fun migrate() {
        favoriteStationsStateUseCase.reloadFromNetwork()
    }
}
