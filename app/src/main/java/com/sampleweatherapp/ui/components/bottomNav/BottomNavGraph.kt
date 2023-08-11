package com.sampleweatherapp.ui.components.bottomNav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sampleweatherapp.ui.FavouriteScreen
import com.sampleweatherapp.ui.WeatherScreen
import com.sampleweatherapp.utilities.LocationManager

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    locationManager: LocationManager, onRequestPermission: (() -> Unit) -> Unit
) {
    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                WeatherScreen(
                    locationManager = locationManager,
                    onRequestPermission = onRequestPermission
                )
            }
        }
        composable(BottomNavItem.Favourites.screen_route) {
            FavouriteScreen()
        }
    }
}