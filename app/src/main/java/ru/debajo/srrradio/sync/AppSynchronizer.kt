package ru.debajo.srrradio.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.joda.time.DateTime
import ru.debajo.srrradio.auth.AuthManager
import ru.debajo.srrradio.auth.AuthState
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.SyncUseCase
import timber.log.Timber

internal class AppSynchronizer(
    private val syncUseCase: SyncUseCase,
    private val authManager: AuthManager,
    private val appStateSnapshotExtractor: AppStateSnapshotExtractor,
    private val appStateSnapshotMerger: AppStateSnapshotMerger,
) {
    fun observeLastSyncDate(): Flow<DateTime?> {
        return authManager.authState.flatMapLatest { state ->
            when (state) {
                is AuthState.Anonymous -> flowOf(null)
                is AuthState.Authenticated -> syncUseCase.observeLastSyncDate(state.user.uid)
            }
        }.catch {
            Timber.e(it)
            emit(null)
        }
    }

    suspend fun sync() {
        val userId = authManager.currentUser?.uid ?: return
        val actualSnapshot = syncUseCase.load(userId)
        val localSnapshot = appStateSnapshotExtractor.extract()
        if (actualSnapshot != null) {
            val mergedSnapshot = appStateSnapshotMerger.merge(localSnapshot, actualSnapshot)
            appStateSnapshotExtractor.applyNewSnapshot(mergedSnapshot)
            syncUseCase.save(userId, mergedSnapshot)
        } else {
            syncUseCase.save(userId, localSnapshot)
        }
    }

    suspend fun deleteSyncData() {
        val userId = authManager.currentUser?.uid ?: return
        runCatchingNonCancellation {
            syncUseCase.delete(userId)
        }
    }
}
