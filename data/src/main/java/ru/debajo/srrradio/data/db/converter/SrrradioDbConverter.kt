package ru.debajo.srrradio.data.db.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import ru.debajo.srrradio.common.utils.inject

internal class SrrradioDbConverter {

    private val json: Json by inject()

    @TypeConverter
    fun toString(input: List<String>): String {
        return json.encodeToString(ListSerializer(String.serializer()), input)
    }

    @TypeConverter
    fun fromString(input: String): List<String> {
        if (input.isEmpty()) {
            return emptyList()
        }

        return runCatching {
            json.decodeFromString(ListSerializer(String.serializer()), input)
        }.getOrElse { emptyList() }
    }
}
