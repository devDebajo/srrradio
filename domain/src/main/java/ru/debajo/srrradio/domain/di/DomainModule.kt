package ru.debajo.srrradio.domain.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCase
import ru.debajo.srrradio.domain.FavoriteStationsStateUseCaseImpl
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.TracksCollectionUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCase
import ru.debajo.srrradio.domain.UpdateFavoriteStationStateUseCaseImpl
import ru.debajo.srrradio.domain.UserLocationUseCase
import ru.debajo.srrradio.domain.UserLocationUseCaseImpl
import ru.debajo.srrradio.domain.repository.SearchStationsRepository
import ru.debajo.srrradio.domain.repository.TracksCollectionRepository

val DomainModule: Module = module {
    factory<SearchStationsUseCase> { get<SearchStationsRepository>() }
    factory<TracksCollectionUseCase> { get<TracksCollectionRepository>() }
    factory<UpdateFavoriteStationStateUseCase> { UpdateFavoriteStationStateUseCaseImpl(get()) }
    single<FavoriteStationsStateUseCase> { FavoriteStationsStateUseCaseImpl(get(), get()) }
    single<UserLocationUseCase> { UserLocationUseCaseImpl(get(), get()) }
}
