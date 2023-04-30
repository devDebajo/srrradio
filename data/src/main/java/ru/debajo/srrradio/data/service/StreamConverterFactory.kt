package ru.debajo.srrradio.data.service

import java.lang.reflect.Type
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
internal class StreamConverterFactory(
    private val mediaType: MediaType,
    private val json: Json,
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val serializer = json.serializersModule.serializer(type)
        return StreamResponseConverter(json, serializer)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val serializer = json.serializersModule.serializer(type)
        return StreamRequestConverter(json, mediaType, serializer)
    }

    private class StreamResponseConverter<T>(
        private val json: Json,
        private val serializer: KSerializer<T>,
    ) : Converter<ResponseBody, T> {
        override fun convert(value: ResponseBody): T {
            return json.decodeFromStream(serializer, value.byteStream())
        }
    }

    private class StreamRequestConverter<T>(
        private val json: Json,
        private val mediaType: MediaType,
        private val serializer: KSerializer<T>,
    ) : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody {
            val string = json.encodeToString(serializer, value)
            return string.toRequestBody(mediaType)
        }
    }
}

fun Json.asConverterFactory(mediaType: MediaType): Converter.Factory {
    return StreamConverterFactory(mediaType, this)
}
