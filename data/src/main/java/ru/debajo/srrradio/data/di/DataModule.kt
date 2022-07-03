package ru.debajo.srrradio.data.di

import android.content.SharedPreferences
import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.db.SrrradioDatabase
import ru.debajo.srrradio.data.db.dao.DbPlaylistDao
import ru.debajo.srrradio.data.db.dao.DbPlaylistMappingDao
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.repository.SearchStationsRepositoryImpl
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.data.usecase.LastStationUseCaseImpl
import ru.debajo.srrradio.data.usecase.LoadPlaylistUseCaseImpl
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.repository.SearchStationsRepository

internal interface DataModule : DataApiInternal {

    fun provideJson(): Json {
        return Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    fun provideServiceHolder(
        json: Json,
        httpClient: OkHttpClient,
        apiHostDiscovery: ApiHostDiscovery
    ): ServiceHolder = ServiceHolder(json, httpClient, apiHostDiscovery)

    fun provideSearchStationsRepository(
        serviceHolder: ServiceHolder
    ): SearchStationsRepository = SearchStationsRepositoryImpl(serviceHolder)

    fun provideLastStationUseCase(sharedPreferences: SharedPreferences): LastStationUseCase = LastStationUseCaseImpl(sharedPreferences)

    fun provideLoadPlaylistUseCase(
        playlistDao: DbPlaylistDao,
        stationDao: DbStationDao
    ): LoadPlaylistUseCase = LoadPlaylistUseCaseImpl(playlistDao, stationDao)

    class Impl(private val dependencies: DataDependencies) : DataModule {

        private val okHttpClient: OkHttpClient by lazy {
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

        private val serviceHolder: ServiceHolder by lazy { provideServiceHolder(json, okHttpClient, apiHostDiscovery) }

        private val database: SrrradioDatabase by lazy { Room.databaseBuilder(dependencies.context, SrrradioDatabase::class.java, "srrradio.db").build() }

        private val dbPlaylistDao: DbPlaylistDao by lazy { database.dbPlaylistDao() }

        private val dbStationDao: DbStationDao by lazy { database.dbStationDao() }

        private val dbPlaylistMappingDao: DbPlaylistMappingDao by lazy { database.dbPlaylistMappingDao() }

        override val json: Json by lazy { provideJson() }

        override val searchStationsRepository: SearchStationsRepository by lazy { provideSearchStationsRepository(serviceHolder) }

        override val sharedPreferences: SharedPreferences
            get() = dependencies.sharedPreferences

        override val apiHostDiscovery: ApiHostDiscovery by lazy { ApiHostDiscovery() }

        override val lastStationUseCase: LastStationUseCase
            get() = provideLastStationUseCase(sharedPreferences)

        override val loadPlaylistUseCase: LoadPlaylistUseCase
            get() = provideLoadPlaylistUseCase(dbPlaylistDao, dbStationDao)
    }
}
