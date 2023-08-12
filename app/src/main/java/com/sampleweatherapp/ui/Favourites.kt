package com.sampleweatherapp.ui

import android.util.Log
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.sampleweatherapp.R
import com.sampleweatherapp.models.City
import com.sampleweatherapp.network.response.Response
import com.sampleweatherapp.ui.components.ErrorScreen
import com.sampleweatherapp.ui.components.LoadingAnimation
import com.sampleweatherapp.ui.components.PlacePicker
import com.sampleweatherapp.ui.theme.cloudy
import com.sampleweatherapp.utilities.ErrorState
import com.sampleweatherapp.viewmodels.WeatherScreenViewModel

@Composable
fun FavouriteScreen(
    background: Color = cloudy,
    weatherViewModel: WeatherScreenViewModel = viewModel(),
    latLng: LatLng = LatLng(-1.286389, 36.817223),
    placesClient: PlacesClient
) {
    val context = LocalContext.current
    val favourites: List<City> = weatherViewModel.getFavourites(context)
    val uiSettings = remember { mutableStateOf(MapUiSettings()) }
    val properties = remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val isPickingLocation = rememberSaveable { mutableStateOf(false) }
    val pickedAddress = rememberSaveable() { mutableStateOf("") }
    val pickedLatLng = rememberSaveable { mutableStateOf(latLng) }

    fun getWeather(latLng: LatLng) {
        weatherViewModel.getForecastWeather(latLng.latitude, latLng.latitude)

    }

    if (isPickingLocation.value) {
        PlacePicker(
            latLng = latLng,
            placesClient = placesClient,
            onPlacePicked = { address: String, coord: LatLng ->
                run {
                    Log.i("Favs", "$coord --> $address")
                    isPickingLocation.value = false
                    pickedAddress.value = address
                    pickedLatLng.value = coord
                    getWeather(coord)
                }
            })
    } else {
        Body(background, favourites, properties, uiSettings, isPickingLocation)
    }
    if (pickedAddress.value.isNotEmpty()) {
        when (val forecastResponse = weatherViewModel.forecastWeatherState.value) {
            is Response.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingAnimation()
                }
            }

            is Response.Success -> {
                if (forecastResponse.data == null) {
                    Log.e("FavouriteScreen", "returned a null weather forecast")
                    ErrorScreen(onClickRetry = { getWeather(pickedLatLng.value) })
                } else {
                    weatherViewModel.addFavourites(context, forecastResponse.data.city)
                }
            }

            is Response.Failure -> {
                ErrorScreen(
                    errorState = ErrorState.ERROR,
                    onClickRetry = { getWeather(pickedLatLng.value) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Body(
    background: Color,
    favourites: List<City>,
    properties: MutableState<MapProperties>,
    uiSettings: MutableState<MapUiSettings>,
    isPickingLocation: MutableState<Boolean>
) {
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
            if (favourites.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxWidth()) {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            properties = properties.value,
                            uiSettings = uiSettings.value
                        )
                        Switch(
                            checked = uiSettings.value.zoomControlsEnabled,
                            onCheckedChange = {
                                uiSettings.value = uiSettings.value.copy(zoomControlsEnabled = it)
                            }
                        )
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(favourites) { item: City ->
                            ListItem(city = item)
                        }
                    }
                }
            } else {
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
            }
        }
    }
}

@Composable
fun ListItem(city: City, modifier: Modifier = Modifier) {
    Log.e("FAVS",city.toString())
    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(modifier.fillMaxWidth()) {
            Text(
                text = city.name, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Text(
                text = city.country + "°", modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), textAlign = TextAlign.End
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                IconButton(
                    onClick = {
                        //todo menu
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                        contentDescription = "meu"
                    )
                }
            }
        }
    }
}