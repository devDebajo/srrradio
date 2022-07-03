package ru.debajo.srrradio.common.di

import android.content.Context

interface CommonDependencies {
    val context: Context

    class Impl(override val context: Context) : CommonDependencies
}
