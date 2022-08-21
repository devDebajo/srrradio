package ru.debajo.srrradio.domain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeout
import timber.log.Timber

interface UserLocationUseCase {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}

internal class UserLocationUseCaseImpl(
    private val context: Context,
    private val locationManager: LocationManager,
) : UserLocationUseCase {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!context.checkPermission(LOCATION_PERMISSION)) {
            return null
        }

        return runCatching {
            withTimeout(20_000) { observeLocation().firstOrNull() }
        }
            .onFailure { Timber.e(it) }
            .recoverCatching { lastCachedLocation() }
            .onFailure { Timber.e(it) }
            .getOrNull()
    }

    private fun Context.checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(LOCATION_PERMISSION)
    private fun lastCachedLocation(): Pair<Double, Double>? {
        return locationManager.getLastKnownLocation(PROVIDER)?.convert()
    }

    @RequiresPermission(LOCATION_PERMISSION)
    private fun observeLocation(): Flow<Pair<Double, Double>> {
        return callbackFlow {
            val listener = LocationListener { trySend(it.convert()) }

            runCatching {
                locationManager.requestLocationUpdates(
                    PROVIDER,
                    2000,
                    100f,
                    listener,
                    Looper.getMainLooper()
                )
            }.onFailure {
                locationManager.removeUpdates(listener)
                cancel("Problem with location", it)
            }

            awaitClose {
                locationManager.removeUpdates(listener)
            }
        }
    }

    private fun Location.convert(): Pair<Double, Double> = latitude to longitude

    private companion object {
        const val PROVIDER = LocationManager.GPS_PROVIDER
    }
}

const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
