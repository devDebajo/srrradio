package ru.debajo.srrradio.domain.repository

import ru.debajo.srrradio.domain.model.Config

interface ConfigRepository {
    suspend fun provide(force: Boolean = false): Config
}
