package com.angelotacoj.apprutaoptimaescape.features.location.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("LogNotTimber")
class LocationViewModel(
    private val app: Application
): AndroidViewModel(app) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(app)

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        Log.d("LocationVM", "startLocationUpdates() llamado")

        val request = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1_000L
        )
            .setMinUpdateDistanceMeters(0f)
            .build()

        Log.d("LocationVM", "LocationRequest: $request")

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                Log.d("LocationVM", "onLocationResult: $result")
                result.lastLocation?.let { loc ->
                    Log.d("LocationVM", "Ubicación recibida: lat=${loc.latitude}, lon=${loc.longitude}")
                    _location.value = loc
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                Log.d("LocationVM", "onLocationAvailability: $availability")
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            Log.d("LocationVM", "requestLocationUpdates: éxito")
        }.addOnFailureListener { ex ->
            Log.e("LocationVM", "requestLocationUpdates: fallo", ex)
        }
    }

    fun stopLocationUpdates(callback: LocationCallback) {
        fusedClient.removeLocationUpdates(callback)
        Log.d("LocationVM", "stopLocationUpdates() llamado")
    }
}