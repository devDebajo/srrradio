package ru.debajo.srrradio.common.di

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.debajo.srrradio.common.GooglePlayInAppUpdateHelper
import ru.debajo.srrradio.common.IntentForResultStarterHolder

val CommonModule: Module = module {
    single { get<Context>().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
    single { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single { get<Context>().getSharedPreferences("srrradio.prefs", Context.MODE_PRIVATE) }
    single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    singleOf(::IntentForResultStarterHolder)
    factoryOf(::GooglePlayInAppUpdateHelper)
    single { AppUpdateManagerFactory.create(get()) }
}
