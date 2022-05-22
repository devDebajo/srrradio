package ru.debajo.srrradio.data.di

import android.content.SharedPreferences
import ru.debajo.srrradio.common.di.CommonApiHolder
import ru.debajo.srrradio.common.di.ModuleDependencies

interface DataDependencies : ModuleDependencies {
    val sharedPreferences: SharedPreferences

    object Impl : DataDependencies {
        override val sharedPreferences: SharedPreferences
            get() = CommonApiHolder.get().sharedPreferences()
    }
}
