package com.sampleweatherapp

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.libraries.places.api.net.PlacesClient
import com.sampleweatherapp.ui.MainScreen
import com.sampleweatherapp.ui.theme.SampleWeatherAppTheme
import com.sampleweatherapp.utilities.LocationManager


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleWeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp(
                        requestLocationPermission = ::requestLocationPermission,
                        locationManager = locationManager,
                        placesClient = placeClient,
                        geocoder = geocoder
                    )
                }
            }
        }

    }

}

@Composable
fun WeatherApp(
    locationManager: LocationManager,
    requestLocationPermission: (onPermissionGranted: () -> Unit) -> Unit,
    placesClient: PlacesClient,
    geocoder: Geocoder
) {
    MainScreen(
        locationManager = locationManager,
        onRequestPermission = requestLocationPermission,
        placesClient = placesClient,
        geocoder = geocoder
    )
}