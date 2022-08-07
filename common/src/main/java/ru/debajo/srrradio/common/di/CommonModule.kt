package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager

internal interface CommonModule : CommonApi {
    class Impl(private val dependencies: CommonDependencies) : CommonModule {
        override val context: Context
            get() = dependencies.context

        override val locationManager: LocationManager by lazy {
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        override val sharedPreferences: SharedPreferences by lazy {
            context.getSharedPreferences("srrradio.prefs", Context.MODE_PRIVATE)
        }

        override val notificationManager: NotificationManager by lazy {
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
}
