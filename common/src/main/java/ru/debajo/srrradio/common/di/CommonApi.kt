package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences

interface CommonApi : ModuleApi {
    fun context(): Context
    fun sharedPreferences(): SharedPreferences
    fun notificationManager(): NotificationManager
}
