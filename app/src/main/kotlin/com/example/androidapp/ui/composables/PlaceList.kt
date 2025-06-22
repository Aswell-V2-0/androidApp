package com.example.androidapp.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidapp.model.Place
import kotlin.collections.forEach

@Composable
fun PlaceList(places: List<Place>, modifier: Modifier = Modifier) {
    places.forEach { place ->
        PlaceCard(place.name, modifier = Modifier.padding(12.dp))
    }
}
