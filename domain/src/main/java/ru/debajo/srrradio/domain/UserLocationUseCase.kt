package ru.debajo.srrradio.domain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeout
import ru.debajo.srrradio.common.utils.hasPermission
import ru.debajo.srrradio.common.utils.runCatchingNonCancellation
import ru.debajo.srrradio.domain.model.LatLng
import timber.log.Timber

interface UserLocationUseCase {
    suspend fun getCurrentLocation(): LatLng?

    fun observeLocation(): Flow<LatLng>

    fun getLastCachedLocation(): LatLng?
}

internal class UserLocationUseCaseImpl(
    private val context: Context,
    private val locationManager: LocationManager,
) : UserLocationUseCase {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LatLng? {
        if (!context.hasPermission(LOCATION_PERMISSION)) {
            return null
        }

        return runCatchingNonCancellation {
            withTimeout(20_000) { observeLocation().firstOrNull() }
        }
            .onFailure { Timber.e(it) }
            .recoverCatching { lastCachedLocation() }
            .onFailure { Timber.e(it) }
            .getOrNull()
    }

    @SuppressLint("MissingPermission")
    override fun observeLocation(): Flow<LatLng> {
        if (!context.hasPermission(LOCATION_PERMISSION)) {
            return emptyFlow()
        }

        return observeLocationInternal()
    }

    @SuppressLint("MissingPermission")
    override fun getLastCachedLocation(): LatLng? {
        if (!context.hasPermission(LOCATION_PERMISSION)) {
            return null
        }

        return lastCachedLocation()
    }

    @RequiresPermission(LOCATION_PERMISSION)
    private fun lastCachedLocation(): LatLng? {
        return locationManager.getLastKnownLocation(PROVIDER)?.convert()
    }

    @RequiresPermission(LOCATION_PERMISSION)
    private fun observeLocationInternal(): Flow<LatLng> {
        return callbackFlow {
            val listener = LocationListener {
                trySend(it.convert())
            }

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

    private fun Location.convert(): LatLng = LatLng(latitude, longitude)

    private companion object {
        val PROVIDER = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocationManager.FUSED_PROVIDER
        } else {
            LocationManager.PASSIVE_PROVIDER
        }
    }
}

const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
