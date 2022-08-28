package ru.debajo.srrradio.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.debajo.srrradio.data.BuildConfig
import ru.debajo.srrradio.data.db.MIGRATIONS
import ru.debajo.srrradio.data.db.SrrradioDatabase
import ru.debajo.srrradio.data.db.dao.DbFavoriteStationDao
import ru.debajo.srrradio.data.db.dao.DbPlaylistDao
import ru.debajo.srrradio.data.db.dao.DbPlaylistMappingDao
import ru.debajo.srrradio.data.db.dao.DbStationDao
import ru.debajo.srrradio.data.db.dao.DbTrackCollectionItemDao
import ru.debajo.srrradio.data.repository.FavoriteStationsRepositoryImpl
import ru.debajo.srrradio.data.repository.SearchStationsRepositoryImpl
import ru.debajo.srrradio.data.repository.TracksCollectionRepositoryImpl
import ru.debajo.srrradio.data.service.ApiHostDiscovery
import ru.debajo.srrradio.data.service.ServiceHolder
import ru.debajo.srrradio.data.sync.SyncRepositoryImpl
import ru.debajo.srrradio.data.usecase.LastPlaylistIdPreference
import ru.debajo.srrradio.data.usecase.LastStationIdPreference
import ru.debajo.srrradio.data.usecase.LastStationUseCaseImpl
import ru.debajo.srrradio.data.usecase.LoadPlaylistUseCaseImpl
import ru.debajo.srrradio.data.usecase.ParseM3uUseCaseImpl
import ru.debajo.srrradio.data.usecase.UserStationUseCaseImpl
import ru.debajo.srrradio.domain.LastStationUseCase
import ru.debajo.srrradio.domain.LoadPlaylistUseCase
import ru.debajo.srrradio.domain.ParseM3uUseCase
import ru.debajo.srrradio.domain.UserStationUseCase
import ru.debajo.srrradio.domain.repository.FavoriteStationsRepository
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.SyncRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

internal interface DataModule : DataApiInternal {

    fun provideJson(): Json {
        return Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
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

    fun provideTracksCollectionRepository(dao: DbTrackCollectionItemDao): TracksCollectionRepository {
        return TracksCollectionRepositoryImpl(dao)
    }

    fun provideLastStationUseCase(
        lastPlaylistIdPreference: LastPlaylistIdPreference,
        lastStationIdPreference: LastStationIdPreference,
    ): LastStationUseCase = LastStationUseCaseImpl(lastPlaylistIdPreference, lastStationIdPreference)

    fun provideLoadPlaylistUseCase(
        playlistDao: DbPlaylistDao,
        stationDao: DbStationDao,
        dbPlaylistMappingDao: DbPlaylistMappingDao,
    ): LoadPlaylistUseCase = LoadPlaylistUseCaseImpl(playlistDao, stationDao, dbPlaylistMappingDao)

    fun provideFavoriteStationsRepository(dbStationDao: DbStationDao, dbFavoriteStationDao: DbFavoriteStationDao): FavoriteStationsRepository {
        return FavoriteStationsRepositoryImpl(dbStationDao, dbFavoriteStationDao)
    }

    fun provideUserStationUseCase(stationDao: DbStationDao): UserStationUseCase = UserStationUseCaseImpl(stationDao)

    fun provideParseM3uUseCase(context: Context): ParseM3uUseCase = ParseM3uUseCaseImpl(context)

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

        private val database: SrrradioDatabase by lazy {
            Room.databaseBuilder(dependencies.context, SrrradioDatabase::class.java, "srrradio.db")
                .addMigrations(*MIGRATIONS)
                .build()
        }

        private val firebaseDatabase: FirebaseDatabase by lazy {
            FirebaseDatabase.getInstance(BuildConfig.REALTIME_DB_PATH)
        }

        private val dbPlaylistDao: DbPlaylistDao by lazy { database.dbPlaylistDao() }

        private val dbStationDao: DbStationDao by lazy { database.dbStationDao() }

        private val dbPlaylistMappingDao: DbPlaylistMappingDao by lazy { database.dbPlaylistMappingDao() }

        private val dbFavoriteStationDao: DbFavoriteStationDao by lazy { database.dbFavoriteStationDao() }

        private val dbTrackCollectionItemDao: DbTrackCollectionItemDao by lazy { database.dbTrackCollectionItemDao() }

        override val json: Json by lazy { provideJson() }

        private val gson: Gson by lazy { Gson() }

        override val searchStationsRepository: SearchStationsRepository by lazy { provideSearchStationsRepository(serviceHolder) }

        override val favoriteStationsRepository: FavoriteStationsRepository by lazy {
            provideFavoriteStationsRepository(dbStationDao, dbFavoriteStationDao)
        }

        override val sharedPreferences: SharedPreferences
            get() = dependencies.sharedPreferences

        override val apiHostDiscovery: ApiHostDiscovery by lazy { ApiHostDiscovery() }

        override val lastStationUseCase: LastStationUseCase
            get() = provideLastStationUseCase(
                lastPlaylistIdPreference = LastPlaylistIdPreference(sharedPreferences),
                lastStationIdPreference = LastStationIdPreference(sharedPreferences),
            )

        override val loadPlaylistUseCase: LoadPlaylistUseCase
            get() = provideLoadPlaylistUseCase(dbPlaylistDao, dbStationDao, dbPlaylistMappingDao)

        override val userStationUseCase: UserStationUseCase
            get() = provideUserStationUseCase(dbStationDao)

        override val parseM3uUseCase: ParseM3uUseCase
            get() = provideParseM3uUseCase(dependencies.context)

        override val tracksCollectionRepository: TracksCollectionRepository
            get() = provideTracksCollectionRepository(dbTrackCollectionItemDao)

        override val syncRepository: SyncRepository
            get() = SyncRepositoryImpl(gson = gson, database = firebaseDatabase)
    }
}
