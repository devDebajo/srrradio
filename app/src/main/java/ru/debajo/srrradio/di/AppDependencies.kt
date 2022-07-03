package ru.debajo.srrradio.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.common.di.ModuleDependencies
import ru.debajo.srrradio.domain.SearchStationsUseCase
import ru.debajo.srrradio.domain.di.DomainApiHolder

internal interface AppDependencies : ModuleDependencies {

    val searchStationsUseCase: SearchStationsUseCase
    val context: Context
    val applicationCoroutineScope: CoroutineScope

    class Impl(override val applicationCoroutineScope: CoroutineScope) : AppDependencies {

        override val searchStationsUseCase: SearchStationsUseCase
            get() = DomainApiHolder.get().searchStationsUseCase()

        override val context: Context
            get() = CommonApiHolder.get().context()
    }
}
