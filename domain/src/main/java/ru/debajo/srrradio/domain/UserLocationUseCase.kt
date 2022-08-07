package ru.debajo.srrradio.domain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull

interface UserLocationUseCase {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}

internal class UserLocationUseCaseImpl(
    private val context: Context,
    private val locationManager: LocationManager,
) : UserLocationUseCase {

    override suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!context.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return null
        }

        return observeLocation().firstOrNull()
    }

    private fun Context.checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun observeLocation(): Flow<Pair<Double, Double>> {
        return callbackFlow {
            val listener = LocationListener {
                trySend(it.latitude to it.longitude)
            }

            locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                2000,
                100f,
                listener,
                Looper.getMainLooper()
            )

            awaitClose {
                locationManager.removeUpdates(listener)
            }
        }
    }
}
