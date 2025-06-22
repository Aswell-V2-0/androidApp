package com.example.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidapp.data.DefaultLocationRepository
import com.example.androidapp.viewmodel.LocationViewModel
import com.google.android.gms.location.LocationServices
import com.example.androidapp.ui.LocationScreen
import com.example.androidapp.ui.theme.AndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRepository = DefaultLocationRepository(fusedLocationClient)

        setContent {
            val locationViewModel = viewModel<LocationViewModel>(
                factory = viewModelFactory {
                    initializer {
                        LocationViewModel(application, locationRepository)
                    }
                }
            )

            AndroidAppTheme {
                LocationScreen(viewModel = locationViewModel)
            }
        }
    }
}
