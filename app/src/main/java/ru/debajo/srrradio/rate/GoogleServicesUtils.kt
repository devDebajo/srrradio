package ru.debajo.srrradio.rate

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class GoogleServicesUtils(private val context: Context) {
    val servicesAvailable: Boolean by lazy {
        runCatching {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            resultCode == ConnectionResult.SUCCESS
        }.getOrElse { false }
    }

    val googlePlayAppInstalled: Boolean by lazy {
        runCatching {
            context.packageManager.resolveActivity(googlePlayIntent, PackageManager.MATCH_DEFAULT_ONLY) != null
        }.getOrElse { false }
    }

    val googlePlayIntent: Intent
        get() = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=ru.debajo.srrradio")
        )
}