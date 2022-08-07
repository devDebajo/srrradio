package ru.debajo.srrradio.common.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager

interface CommonApi : ModuleApi {
    val context: Context
    val locationManager: LocationManager
    val sharedPreferences: SharedPreferences
    val notificationManager: NotificationManager
}
