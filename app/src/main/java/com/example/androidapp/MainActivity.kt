package com.example.androidapp

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.androidapp.ui.theme.AndroidAppTheme
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ShowLocation()
                }
            }
        }
    }
}

@Composable
fun LocationPermissionApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationText by remember { mutableStateOf("") }
    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher to request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
        if (granted) {
            getLastKnownLocation(fusedLocationClient) { location ->
                locationText = location ?: "Location not available"
            }
        } else {
            locationText = "Permission not granted"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text="pinPointed", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (isPermissionGranted) {
                // Permission already granted, get location
                getLastKnownLocation(fusedLocationClient) { location ->
                    locationText = location ?: "Location not available"
                }
                // checkLocationSettings(activity)
            } else {
                // Request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }) {
            Text("Get Location")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = locationText)
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION])
fun getLastKnownLocation(
    fusedLocationClient: FusedLocationProviderClient, onResult: (String?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            val text = "Lat: ${location.latitude}, Lng: ${location.longitude}"
            onResult(text)
        } else {
            onResult("Please turn on location service")
        }
    }
}

fun checkLocationSettings(activity: Activity) {
    val locationRequest = LocationRequest.create()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true) // Important to show the dialog

    val settingsClient = LocationServices.getSettingsClient(activity)
    val task = settingsClient.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        // All location settings are satisfied, proceed with location request
    }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                // Show the dialog to prompt user to enable location
                exception.startResolutionForResult(activity, 1001)
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore the error
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowLocation() {
    AndroidAppTheme {
        LocationPermissionApp()
    }
}
