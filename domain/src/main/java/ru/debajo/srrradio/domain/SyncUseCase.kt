package ru.debajo.srrradio.domain

import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import ru.debajo.srrradio.domain.model.AppStateSnapshot

interface SyncUseCase {

    fun observeLastSyncDate(userId: String): Flow<DateTime?>

    suspend fun save(userId: String, snapshot: AppStateSnapshot)

    suspend fun delete(userId: String)

    suspend fun load(userId: String): AppStateSnapshot?
}
