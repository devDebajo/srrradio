package ru.debajo.srrradio.data.di

import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.config.ConfigRepositoryImpl
import ru.debajo.srrradio.data.db.MIGRATIONS
import ru.debajo.srrradio.data.db.SrrradioDatabase
import ru.debajo.srrradio.data.repository.FavoriteStationsRepositoryImpl
import ru.debajo.srrradio.data.repository.SearchStationsRepositoryImpl
import ru.debajo.srrradio.data.repository.StationsForMapLastUpdatePreference
import ru.debajo.srrradio.data.repository.TracksCollectionRepositoryImpl
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.data.sync.SyncRepositoryImpl
import ru.debajo.srrradio.data.usecase.CheckAppUpdateUseCase
import ru.debajo.srrradio.data.usecase.LastPlaylistIdPreference
import ru.debajo.srrradio.data.usecase.LastStationIdPreference
import ru.debajo.srrradio.data.usecase.LastStationUseCaseImpl
import ru.debajo.srrradio.data.usecase.LoadPlaylistUseCaseImpl
import ru.debajo.srrradio.data.usecase.ParseM3uUseCaseImpl
import ru.debajo.srrradio.data.usecase.RecommendationsUseCase
import ru.debajo.srrradio.data.usecase.UserStationUseCaseImpl
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.SyncUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.repository.ConfigRepository
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

val DataModule: Module = module {
    single {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
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
    single {
        Room.databaseBuilder(get(), SrrradioDatabase::class.java, "srrradio.db")
            .addMigrations(*MIGRATIONS)
            .build()
    }
    single { FirebaseDatabase.getInstance(BuildConfig.REALTIME_DB_PATH) }
    single { FirebaseRemoteConfig.getInstance() }
    single { get<SrrradioDatabase>().dbPlaylistDao() }
    single { get<SrrradioDatabase>().dbStationDao() }
    single { get<SrrradioDatabase>().dbPlaylistMappingDao() }
    single { get<SrrradioDatabase>().dbFavoriteStationDao() }
    single { get<SrrradioDatabase>().dbTrackCollectionItemDao() }
    single {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
    single { Gson() }
    single<SearchStationsRepository> { SearchStationsRepositoryImpl(get(), get(), get()) }
    single<FavoriteStationsRepository> { FavoriteStationsRepositoryImpl(get(), get()) }
    singleOf(::ApiHostDiscovery)

    factory { LastPlaylistIdPreference(get()) }
    factory { LastStationIdPreference(get()) }
    factory { StationsForMapLastUpdatePreference(get()) }
    factory<LastStationUseCase> { LastStationUseCaseImpl(get(), get()) }
    factory<LoadPlaylistUseCase> { LoadPlaylistUseCaseImpl(get(), get(), get()) }
    factory<UserStationUseCase> { UserStationUseCaseImpl(get()) }
    factory<ParseM3uUseCase> { ParseM3uUseCaseImpl(get()) }
    factoryOf(::RecommendationsUseCase)
    factory<TracksCollectionRepository> { TracksCollectionRepositoryImpl(get()) }
    factory<SyncUseCase> { SyncRepositoryImpl(get(), get()) }
    single<ConfigRepository> { ConfigRepositoryImpl(get(), get(), get()) }
    singleOf(::CheckAppUpdateUseCase)
}
