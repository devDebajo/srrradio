package ru.debajo.srrradio.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.joda.time.DateTime
import ru.debajo.srrradio.auth.AuthManagerProvider
import ru.debajo.srrradio.auth.AuthState
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.SyncUseCase
import timber.log.Timber

internal class AppSynchronizer(
    private val syncUseCase: SyncUseCase,
    private val authManagerProvider: AuthManagerProvider,
    private val appStateSnapshotExtractor: AppStateSnapshotExtractor,
    private val appStateSnapshotMerger: AppStateSnapshotMerger,
) {
    fun observeLastSyncDate(): Flow<DateTime?> {
        return flow {
            authManagerProvider().authState.flatMapLatest { state ->
                when (state) {
                    is AuthState.Unavailable,
                    is AuthState.Anonymous -> flowOf(null)
                    is AuthState.Authenticated -> syncUseCase.observeLastSyncDate(state.user.uid)
                }
            }.catch {
                Timber.e(it)
                emit(null)
            }.also { emitAll(it) }
        }
    }

    suspend fun sync() {
        val userId = authManagerProvider().currentUser?.uid ?: return
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
        val userId = authManagerProvider().currentUser?.uid ?: return
        runCatchingNonCancellation {
            syncUseCase.delete(userId)
        }
    }
}
