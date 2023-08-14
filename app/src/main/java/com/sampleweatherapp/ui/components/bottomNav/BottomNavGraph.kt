package com.sampleweatherapp.ui.components.bottomNav

import android.location.Geocoder
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.libraries.places.api.net.PlacesClient
import com.sampleweatherapp.ui.FavouriteScreen
import com.sampleweatherapp.ui.WeatherScreen
import com.sampleweatherapp.ui.theme.cloudy
import com.sampleweatherapp.utilities.LocationManager


var backgroundColor: Color = cloudy

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    locationManager: LocationManager, onRequestPermission: (() -> Unit) -> Unit,
    placesClient: PlacesClient, geocoder: Geocoder
) {
    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                WeatherScreen(
                    locationManager = locationManager,
                    onRequestPermission = onRequestPermission,
                    setBackground = ::setBackground
                )
            }
        }
        composable(BottomNavItem.Favourites.screen_route) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                FavouriteScreen(
                    background = backgroundColor,
                    placesClient = placesClient,
                    geocoder = geocoder,
                    locationManager = locationManager,
                    onRequestPermission = onRequestPermission
                )
            }
        }
    }
}

fun setBackground(color: Color) {
    backgroundColor = color
}