package ru.debajo.srrradio.di

import android.content.Context
import ru.debajo.srrradio.common.di.ModuleDependencies
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.di.DomainApiHolder

internal interface AppDependencies : ModuleDependencies {

    val searchStationsUseCase: SearchStationsUseCase
    val context: Context

    class Impl(override val context: Context) : AppDependencies {
        override val searchStationsUseCase: SearchStationsUseCase
            get() = DomainApiHolder.get().searchStationsUseCase()
    }
}
