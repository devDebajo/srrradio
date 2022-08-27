package ru.debajo.srrradio.data.sync

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.joda.time.DateTime
import ru.debajo.srrradio.common.model.IsDebug
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
    private val isDebug: IsDebug,
    private val json: Json
) : SyncRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    override suspend fun save(userId: String, snapshot: AppStateSnapshot) {
        val toSave = snapshot.toRemote()
        TODO("Not yet implemented")
    }

    override suspend fun delete(userId: String) {
        database.getReference(createPath(userId)).removeValue().asDeferred().await()
    }

    override suspend fun load(userId: String): AppStateSnapshot? {
        TODO("Not yet implemented")
    }

    private suspend fun <T : Any?> DatabaseReference.setValueAsync(value: T, serializer: KSerializer<T>) {
        val element = json.encodeToJsonElement(serializer, value)
        setValueAsync(element)
    }

    private suspend fun DatabaseReference.setValueAsync(value: Any?) {
        setValue(value).asDeferred().await()
    }

    private fun createPath(userId: String): String {
        val prodOrDebugKey = if (isDebug.isDebug) "debug" else "prod"
        return "sync_data/${prodOrDebugKey}/${userId}"
    }

    private fun RemoteDbAppStateSnapshot.toDomain(): AppStateSnapshot {
        val timestamp = DateTime.parse(createTimestamp)
        return AppStateSnapshot(
            dynamicIcon = dynamicIcon.timestampedAt(timestamp),
            themeCode = themeCode.timestampedAt(timestamp),
            autoSendErrors = autoSendErrors.timestampedAt(timestamp),

            collection = collection.mapNotNull { it.toDomain() },
            favoriteStations = favoriteStations.mapNotNull { it.toDomain() },
        )
    }

    private fun AppStateSnapshot.toRemote(): RemoteDbAppStateSnapshot {
        val now = DateTime.now()
        return RemoteDbAppStateSnapshot(
            dynamicIcon = dynamicIcon.value,
            themeCode = themeCode.value,
            autoSendErrors = autoSendErrors.value,

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
            image = image,
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
}
