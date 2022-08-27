package ru.debajo.srrradio.data.sync

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.domain.model.CollectionItem
import ru.debajo.srrradio.domain.model.Station
import ru.debajo.srrradio.domain.repository.SyncRepository

internal class SyncRepositoryImpl(
    private val json: Json
) : SyncRepository {

    private val database = FirebaseDatabase.getInstance()

    override suspend fun getFavoriteStations(userId: String): List<Station> {
        TODO()
    }

    override suspend fun saveFavoriteStations(userId: String, stations: List<Station>) {
        database.getReference("prod/favorite_stations/$userId")
        TODO("Not yet implemented")
    }

    override suspend fun getCollection(userId: String): List<CollectionItem> {
        TODO("Not yet implemented")
    }

    override suspend fun saveCollection(userId: String, collection: List<CollectionItem>) {
        TODO("Not yet implemented")
    }

    private suspend fun <T : Any?> DatabaseReference.setValueAsync(value: T, serializer: KSerializer<T>) {
        val element = json.encodeToJsonElement(serializer, value)
        setValueAsync(element)
    }

    private suspend fun DatabaseReference.setValueAsync(value: Any?) {
        setValue(value).asDeferred().await()
    }
}
