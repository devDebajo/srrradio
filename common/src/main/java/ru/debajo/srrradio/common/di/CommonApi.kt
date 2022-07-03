package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences

interface CommonApi : ModuleApi {
    val context: Context
    val sharedPreferences: SharedPreferences
    val notificationManager: NotificationManager
}
