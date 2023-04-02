package ru.debajo.srrradio.data.sync

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.model.RemoteDbAppStateSnapshot
import ru.debajo.srrradio.data.model.RemoteDbStation
import ru.debajo.srrradio.data.model.RemoteDbTrackCollectionItem
import ru.debajo.srrradio.domain.model.AppStateSnapshot
import ru.debajo.srrradio.domain.model.CollectionItem
import ru.debajo.srrradio.domain.model.LatLng
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.model.timestampedAt
import ru.debajo.srrradio.domain.repository.SyncRepository

internal class SyncRepositoryImpl(
    private val gson: Gson,
    private val database: FirebaseDatabase,
) : SyncRepository {

    override fun observeLastSyncDate(userId: String): Flow<DateTime?> {
        return database.getReference("${createPath(userId)}/createTimestamp").observe()
            .map {
                runCatching { (it.value as? String)?.let { DateTime.parse(it) } }
                    .getOrNull()
            }
    }

    override suspend fun save(userId: String, snapshot: AppStateSnapshot) {
        val toSave = snapshot.toRemote()
        val json = gson.toJson(toSave)
        val map = gson.fromJson<Map<String, Any?>>(json, typeToken<Map<String, Any?>>())
        database.getReference(createPath(userId)).setValueAsync(map)
    }

    override suspend fun delete(userId: String) {
        database.getReference(createPath(userId)).removeValue().await()
    }

    override suspend fun load(userId: String): AppStateSnapshot? {
        return runCatchingNonCancellation {
            database.getReference(createPath(userId)).getOnce().parseToRemote().toDomain()
        }.getOrNull()
    }

    private fun DataSnapshot.parseToRemote(): RemoteDbAppStateSnapshot {
        val json = gson.toJson(value)
        return gson.fromJson(json, RemoteDbAppStateSnapshot::class.java)
    }

    private suspend fun DatabaseReference.setValueAsync(value: Any?) {
        setValue(value).await()
    }

    private fun createPath(userId: String): String {
        val prodOrDebugKey = if (BuildConfig.DEBUG) "debug" else "prod"
        return "sync_data/${prodOrDebugKey}/${userId}"
    }

    private fun RemoteDbAppStateSnapshot.toDomain(): AppStateSnapshot {
        val timestamp = DateTime.parse(createTimestamp)
        return AppStateSnapshot(
            themeCode = themeCode.timestampedAt(timestamp),
            autoSendErrors = autoSendErrors.timestampedAt(timestamp),
            snowFall = snowFall.timestampedAt(timestamp),

            collection = collection.mapNotNull { it.toDomain() },
            favoriteStations = favoriteStations.mapNotNull { it.toDomain() },
        )
    }

    private fun AppStateSnapshot.toRemote(): RemoteDbAppStateSnapshot {
        val now = DateTime.now()
        return RemoteDbAppStateSnapshot(
            themeCode = themeCode.value,
            autoSendErrors = autoSendErrors.value,
            snowFall = snowFall.value,

            collection = collection.map { it.toRemote() },
            favoriteStations = favoriteStations.map { it.toRemote() },

            createTimestamp = now.toString(),
        )
    }

    private fun CollectionItem.toRemote(): RemoteDbTrackCollectionItem {
        return RemoteDbTrackCollectionItem(
            name = track,
            stationId = stationId,
            stationName = stationName,
        )
    }

    private fun Station.toRemote(): RemoteDbStation {
        return RemoteDbStation(
            id = id,
            name = name,
            stream = stream,
            image = image?.takeIf { it.isNotEmpty() },
            latitude = location?.latitude,
            longitude = location?.longitude,
        )
    }

    private fun RemoteDbTrackCollectionItem.toDomain(): CollectionItem? {
        return CollectionItem(
            track = name ?: return null,
            stationId = stationId ?: return null,
            stationName = stationName ?: return null,
        )
    }

    private fun RemoteDbStation.toDomain(): Station? {
        return Station(
            id = id ?: return null,
            name = name ?: return null,
            stream = stream ?: return null,
            image = image,
            location = LatLng.from(latitude, longitude)
        )
    }

    @Suppress("ThrowableNotThrown")
    private suspend fun DatabaseReference.getOnce(): DataSnapshot {
        return suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            }
            addListenerForSingleValueEvent(listener)

            continuation.invokeOnCancellation { removeEventListener(listener) }
        }
    }

    @Suppress("ThrowableNotThrown")
    private fun DatabaseReference.observe(): Flow<DataSnapshot> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    cancel("The firebase error", error.toException())
                }
            }

            addValueEventListener(listener)

            awaitClose { removeEventListener(listener) }
        }
    }

    private inline fun <reified T> typeToken(): Type {
        return object : TypeToken<T>() {}.type
    }
}
