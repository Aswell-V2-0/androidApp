package com.example.androidapp.data

import android.location.Location
import com.example.androidapp.model.Place
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.math.*
import kotlin.coroutines.resume

interface LocationRepository {
    suspend fun getLastKnownLocation(): Location?
    fun getNearbyPlaces(currentLocation: Location, radiusInKm: Double = 1.0): List<Place>
}

class DefaultLocationRepository(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    private val places = listOf(
        Place("Bretton", 5.05818, -75.48760),
        Place("Parque de la Mujer", 5.06510, -75.49951),
        Place("Mirador de Chipre", 5.07476, -75.52764)
    )

    override suspend fun getLastKnownLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }

    override fun getNearbyPlaces(currentLocation: Location, radiusInKm: Double): List<Place> {
        return places.filter {
            val distance = haversine(
                currentLocation.latitude,
                currentLocation.longitude,
                it.latitude,
                it.longitude
            )
            distance <= radiusInKm
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
