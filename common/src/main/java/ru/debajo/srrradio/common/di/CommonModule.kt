package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences

internal interface CommonModule : CommonApi {
    class Impl(private val dependencies: CommonDependencies) : CommonModule {
        override val context: Context
            get() = dependencies.context

        override val sharedPreferences: SharedPreferences by lazy {
            context.getSharedPreferences("srrradio.prefs", Context.MODE_PRIVATE)
        }

        override val notificationManager: NotificationManager by lazy {
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
}
