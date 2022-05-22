package ru.debajo.srrradio.data.di

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.repository.SearchStationsRepositoryImpl
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal val DataDiModule = module {
    single {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BASIC
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single { ServiceHolder(get(), get()) }
    single<SearchStationsRepository> { SearchStationsRepositoryImpl(get()) }
}
