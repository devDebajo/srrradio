package ru.debajo.srrradio.data.di

import android.content.Context
import android.content.SharedPreferences
import ru.debajo.srrradio.common.di.CommonApiHolder

interface DataDependencies {
    val sharedPreferences: SharedPreferences
    val context: Context

    object Impl : DataDependencies {
        override val sharedPreferences: SharedPreferences
            get() = CommonApiHolder.get().sharedPreferences

        override val context: Context
            get() = CommonApiHolder.get().context
    }
}
