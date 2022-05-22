package ru.debajo.srrradio.di

import android.content.Context
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.common.di.ModuleDependencies
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.di.DomainApiHolder

internal interface AppDependencies : ModuleDependencies {

    val searchStationsUseCase: SearchStationsUseCase
    val context: Context

    object Impl : AppDependencies {
        override val searchStationsUseCase: SearchStationsUseCase
            get() = DomainApiHolder.get().searchStationsUseCase()

        override val context: Context
            get() = CommonApiHolder.get().context()
    }
}
