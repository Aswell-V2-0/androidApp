package com.example.androidapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.data.LocationRepository
import com.example.androidapp.model.Place
import kotlinx.coroutines.launch

class LocationViewModel(
    app: Application,
    private val locationRepository: LocationRepository
) : AndroidViewModel(app) {

    private val _locationText = mutableStateOf("Permission not granted")
    val locationText: State<String> = _locationText

    private val _userLocation = mutableStateOf<Location?>(null)
    val userLocation: State<Location?> = _userLocation

    private val _nearbyPlaces = mutableStateOf<List<Place>>(emptyList())
    val nearbyPlaces: State<List<Place>> = _nearbyPlaces

    fun fetchLocationAndPlaces() {
        viewModelScope.launch {
            val location = locationRepository.getLastKnownLocation()
            _userLocation.value = location
            _locationText.value = location?.let {
                "Lat: ${it.latitude}, Lng: ${it.longitude}"
            } ?: "Please enable location service"

            _nearbyPlaces.value = location?.let {
                locationRepository.getNearbyPlaces(it)
            } ?: emptyList()
        }
    }
}
