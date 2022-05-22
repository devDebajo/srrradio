package ru.debajo.srrradio.di

import ru.debajo.srrradio.common.di.ModuleDependencies
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.di.DomainApiHolder

internal interface AppDependencies : ModuleDependencies {

    val searchStationsUseCase: SearchStationsUseCase

    object Impl : AppDependencies {
        override val searchStationsUseCase: SearchStationsUseCase
            get() = DomainApiHolder.get().searchStationsUseCase()
    }
}
