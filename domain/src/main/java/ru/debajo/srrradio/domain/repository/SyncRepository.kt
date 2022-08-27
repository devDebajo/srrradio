package ru.debajo.srrradio.domain.repository

import ru.debajo.srrradio.domain.model.AppStateSnapshot

interface SyncRepository {
    suspend fun save(userId: String, snapshot: AppStateSnapshot)

    suspend fun delete(userId: String)

    suspend fun load(userId: String): AppStateSnapshot?
}
