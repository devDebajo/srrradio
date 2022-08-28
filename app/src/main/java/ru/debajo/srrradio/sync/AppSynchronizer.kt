package ru.debajo.srrradio.sync

import ru.debajo.srrradio.auth.AuthManager
import ru.debajo.srrradio.domain.SyncUseCase

internal class AppSynchronizer(
    private val syncUseCase: SyncUseCase,
    private val authManager: AuthManager,
    private val appStateSnapshotExtractor: AppStateSnapshotExtractor,
    private val appStateSnapshotMerger: AppStateSnapshotMerger,
) {
    suspend fun sync() {
        val userId = authManager.currentUser?.uid ?: return
        val actualSnapshot = syncUseCase.load(userId) ?: return
        val localSnapshot = appStateSnapshotExtractor.extract()
        val mergedSnapshot = appStateSnapshotMerger.merge(localSnapshot, actualSnapshot)
        appStateSnapshotExtractor.applyNewSnapshot(mergedSnapshot)
        syncUseCase.save(userId, mergedSnapshot)
    }

    suspend fun deleteSyncData() {
        val userId = authManager.currentUser?.uid ?: return
        syncUseCase.delete(userId)
    }
}
