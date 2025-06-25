package com.example.androidapp.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidapp.model.Place
import kotlin.collections.forEach

@Composable
fun PlaceList(places: List<Place>, modifier: Modifier = Modifier) {
    Column(
        modifier.height(500.dp).verticalScroll(rememberScrollState())
    ) {
        places.forEach { place ->
            PlaceCard(place.name, modifier = Modifier.padding(12.dp))
        }
    }
}
