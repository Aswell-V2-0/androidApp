package com.example.androidapp.data

import android.location.Location
import com.example.androidapp.model.Place
import com.example.androidapp.network.PlaceApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.JsonParser
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import kotlin.coroutines.resume

interface LocationRepository {
    suspend fun getLastKnownLocation(): Location?
    suspend fun getNearbyPlaces(currentLocation: Location, radiusInKm: Int = 1000): List<Place>
}

class DefaultLocationRepository(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val apiService: PlaceApiService
) : LocationRepository {

    /* private val places = listOf(
         Place("Bretton", 5.05818, -75.48760),
         Place("Parque de la Mujer", 5.06510, -75.49951),
         Place("Mirador de Chipre", 5.07476, -75.52764)
     )*/

    override suspend fun getLastKnownLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resume(null) }
        }

    override suspend fun getNearbyPlaces(currentLocation: Location, radiusInKm: Int): List<Place> {
        val filter = "circle:${currentLocation.longitude},${currentLocation.latitude},$radiusInKm"
        val bias = "proximity:${currentLocation.longitude},${currentLocation.latitude}"
        val response = apiService.getPlaces(filter = filter, bias = bias)
        var places = listOf<Place>()

        if (response.isSuccessful && response.body() != null) {
            places = this.extractPlacesFromCustomJson(response.body()!!)
        }

        return places
    }

    private fun extractPlacesFromCustomJson(responseBody: ResponseBody): List<Place> {
        var places = mutableListOf<Place>()
        val jsonString = responseBody.string()

        // Parse the JSON string
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val dataArray = jsonObject.getAsJsonArray("features")

        for (item in dataArray) {
            val itemObject = item.asJsonObject

            val properties = itemObject.getAsJsonObject("properties")
            val name = properties.get("name")?.asString ?: continue
            val lat = properties.get("lat")?.asDouble ?: continue
            val lng = properties.get("lon")?.asDouble ?: continue

            val place = Place(name = name, latitude = lat, longitude = lng)
            places.add(place)
        }

        return places
    }
}
