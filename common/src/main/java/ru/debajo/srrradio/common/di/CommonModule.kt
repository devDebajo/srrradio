package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import org.koin.core.module.Module
import org.koin.dsl.module

val CommonModule: Module = module {
    single { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single { get<Context>().getSharedPreferences("srrradio.prefs", Context.MODE_PRIVATE) }
    single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
}
