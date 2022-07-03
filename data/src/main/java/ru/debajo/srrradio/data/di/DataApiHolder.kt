package ru.debajo.srrradio.data.di

import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.common.di.ModuleApiHolder
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.db.SrrradioDatabase
import ru.debajo.srrradio.data.repository.SearchStationsRepositoryImpl
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.data.usecase.LastStationUseCaseImpl
import ru.debajo.srrradio.data.usecase.LoadPlaylistUseCaseImpl
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

object DataApiHolder : ModuleApiHolder<DataApi, DataDependencies>() {
    override val koinModules: List<Module> = listOf(
        module {
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
            single { ServiceHolder(get(), get(), get()) }
            single<SearchStationsRepository> { SearchStationsRepositoryImpl(get()) }
            single { ApiHostDiscovery() }

            single { Room.databaseBuilder(get(), SrrradioDatabase::class.java, "srrradio.db").build() }
            single { get<SrrradioDatabase>().dbPlaylistDao() }
            single { get<SrrradioDatabase>().dbStationDao() }
            single { get<SrrradioDatabase>().dbPlaylistMappingDao() }

            factory<LastStationUseCase> { LastStationUseCaseImpl(get()) }
            factory<LoadPlaylistUseCase> { LoadPlaylistUseCaseImpl(get(), get()) }
        }
    )

    override val dependencies: DataDependencies = DataDependencies.Impl

    internal val internalApi: DataApi
        get() = get()
}
