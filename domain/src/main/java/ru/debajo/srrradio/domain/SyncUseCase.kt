package ru.debajo.srrradio.domain

import ru.debajo.srrradio.domain.model.AppStateSnapshot

interface SyncUseCase {
    suspend fun save(userId: String, snapshot: AppStateSnapshot)

    suspend fun delete(userId: String)

    suspend fun load(userId: String): AppStateSnapshot?
}
