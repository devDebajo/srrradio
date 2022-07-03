package ru.debajo.srrradio.data.db.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.data.di.DataApiHolder

internal class SrrradioDbConverter {

    private val json: Json by lazy { DataApiHolder.internalApi.json }

    @TypeConverter
    fun toString(input: List<String>): String {
        return json.encodeToString(ListSerializer(String.serializer()), input)
    }

    @TypeConverter
    fun fromString(input: String): List<String> {
        return json.decodeFromString(ListSerializer(String.serializer()), input)
    }
}
