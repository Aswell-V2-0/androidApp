package com.example.androidapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.androidapp.ui.composables.PlaceList
import com.example.androidapp.viewmodel.LocationViewModel

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationText by viewModel.locationText
    val places by viewModel.nearbyPlaces

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted.value = granted
        if (granted) {
            viewModel.fetchLocationAndPlaces()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PinPointed", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (permissionGranted.value) {
                viewModel.fetchLocationAndPlaces()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }) {
            Text("Show nearby places")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = locationText)
        Spacer(modifier = Modifier.height(16.dp))

        if(places.isNotEmpty()) {
            PlaceList(places)
        }
    }
}
