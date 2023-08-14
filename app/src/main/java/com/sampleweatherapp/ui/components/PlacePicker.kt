package com.sampleweatherapp.ui.components

import android.location.Geocoder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.sampleweatherapp.ui.theme.black
import com.sampleweatherapp.ui.theme.white
import com.sampleweatherapp.viewmodels.AutocompleteResult
import com.sampleweatherapp.viewmodels.LocationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlacePicker(
    latLng: LatLng,
    locationModel: LocationViewModel = viewModel(),
    placesClient: PlacesClient,
    geocoder: Geocoder,
    onPlacePicked: (address: String, latLng: LatLng) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
    val mapUiSettings = remember { mutableStateOf(MapUiSettings()) }
    val mapProperties = remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf("") }
    locationModel.setPlacesClient(placesClient)
    locationModel.setLatLng(latLng)
    locationModel.geoCoder = geocoder
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(locationModel.currentLatLong) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(locationModel.currentLatLong))
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            locationModel.getAddress(cameraPositionState.position.target)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings.value,
            properties = mapProperties.value,
            onMapClick = {
                scope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLng(it))
                }
            }
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AnimatedVisibility(
                    locationModel.locationAutofill.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(locationModel.locationAutofill) { item: AutocompleteResult ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                        text.value = item.address
                                        locationModel.locationAutofill.clear()
                                        locationModel.getCoordinates(item) {
                                            scope.launch {
                                                onPlacePicked(
                                                    text.value,
                                                    locationModel.currentLatLong
                                                )
                                            }
                                        }

                                    }
                            ) {
                                Text(item.address, color = if (isDarkTheme) white else black)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                SearchPlace(text, locationModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlace(text: MutableState<String>, locationModel: LocationViewModel) {
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
            locationModel.searchPlaces(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
    )
}