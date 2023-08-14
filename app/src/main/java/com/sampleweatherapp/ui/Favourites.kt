package com.sampleweatherapp.ui

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sampleweatherapp.R
import com.sampleweatherapp.models.City
import com.sampleweatherapp.models.Coordinates
import com.sampleweatherapp.ui.components.LoadingAnimation
import com.sampleweatherapp.ui.components.PlacePicker
import com.sampleweatherapp.ui.theme.cloudy
import com.sampleweatherapp.utilities.LocationManager
import com.sampleweatherapp.utilities.hasLocationPermission
import com.sampleweatherapp.utilities.isLocationEnabled
import com.sampleweatherapp.viewmodels.WeatherScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FavouriteScreen(
    background: Color = cloudy,
    weatherViewModel: WeatherScreenViewModel = viewModel(),
    locationManager: LocationManager,
    onRequestPermission: (onPermissionGranted: () -> Unit) -> Unit,
    placesClient: PlacesClient,
    geocoder: Geocoder
) {
    val context = LocalContext.current
    val isPickingLocation = rememberSaveable { mutableStateOf(false) }
    val newFav = rememberSaveable { mutableStateOf(false) }
    val gotCurrentLocation = rememberSaveable { mutableStateOf(false) }
    val pickedAddress = rememberSaveable { mutableStateOf("") }
    val pickedLatLng = rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }
    weatherViewModel.getFavourites(context)

    fun launch() {
        if (hasLocationPermission(context)) {
            if (isLocationEnabled(context)) {
                locationManager.getLastLocation(onSuccess = { lat: Double, lon: Double ->
                    pickedLatLng.value = LatLng(lat, lon)
                    gotCurrentLocation.value = true
                })
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.turn_on_location),
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            onRequestPermission { launch() }
        }
    }

    launch()

    if (isPickingLocation.value) {
        PlacePicker(
            latLng = pickedLatLng.value,
            placesClient = placesClient,
            geocoder = geocoder,
            onPlacePicked = { address: String, coord: LatLng ->
                run {
                    isPickingLocation.value = false
                    pickedAddress.value = address
                    pickedLatLng.value = coord
                    newFav.value = true
                    val location = address.split(",").first()
                    val country = address.split(", ").last()
                    val city =
                        City(
                            0,
                            location,
                            Coordinates(lat = coord.latitude, lon = coord.longitude),
                            country
                        )
                    weatherViewModel.addFavourites(context, city)
                }
            })
    } else {
        if (gotCurrentLocation.value) {
            Body(
                background = background,
                isPickingLocation = isPickingLocation,
                weatherViewModel = weatherViewModel,
                latLng = pickedLatLng.value,
                context = context
            )
        } else {
            LoadingAnimation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Body(
    context: Context,
    background: Color,
    isPickingLocation: MutableState<Boolean>,
    weatherViewModel: WeatherScreenViewModel,
    latLng: LatLng,
) {

    val scope = rememberCoroutineScope()
    val uiSettings = remember { mutableStateOf(MapUiSettings()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.favourites)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = {
                        isPickingLocation.value = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "add Favourites"
                        )
                    }
                }

            )
            weatherViewModel.favouriteState.value.let {
                if (it.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No data found",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Press the + button on the top right to add your favourite location",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxWidth()) {
                            MapScreen(
                                latLng = latLng,
                                uiSettings = uiSettings,
                                favourites = it
                            )
                            Switch(
                                modifier = Modifier.padding(start = 16.dp),
                                checked = uiSettings.value.zoomControlsEnabled,
                                onCheckedChange = { enabled: Boolean ->
                                    uiSettings.value =
                                        uiSettings.value.copy(zoomControlsEnabled = enabled)

                                }
                            )
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 16.dp)
                        ) {
                            Log.e("FAVS", it.toString())
                            items(it) { item: City ->
                                ListItem(
                                    city = item,
                                    weatherViewModel = weatherViewModel,
                                    context = context,
                                    scope = scope
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(
    city: City,
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherScreenViewModel,
    context: Context,
    scope: CoroutineScope
) {
    Row(modifier.fillMaxWidth()) {
        Text(
            text = city.name, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
        Text(
            text = city.country, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .weight(1f), textAlign = TextAlign.End
        )
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(start = 10.dp)) {
            IconButton(
                onClick = {
                    scope.launch {
                        weatherViewModel.removeFavourite(context, city)
                    }
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_delete_outline_24),
                    contentDescription = "menu"
                )
            }
        }
    }
}

@Composable
fun MapScreen(
    latLng: LatLng,
    uiSettings: MutableState<MapUiSettings>,
    favourites: List<City>
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 10f)
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        uiSettings = uiSettings.value,
    ) {
        favourites.forEach { city: City ->
            Marker(
                state = MarkerState(
                    position = LatLng(
                        city.coordinates.lat, city.coordinates.lon
                    )
                ),
                title = city.name,
            )
        }
    }
}