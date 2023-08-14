package com.sampleweatherapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sampleweatherapp.models.City
import com.sampleweatherapp.ui.WeatherScreen
import com.sampleweatherapp.ui.theme.SampleWeatherAppTheme
import com.sampleweatherapp.utilities.ARG_CITY
import com.sampleweatherapp.utilities.LocationManager

class FavouriteWeatherActivity : BaseActivity() {

    companion object {
        fun start(context: Context, city: City) {
            val intent = Intent(context, FavouriteWeatherActivity::class.java)
            val b = Bundle()
            b.putSerializable(ARG_CITY, city)
            intent.putExtras(b)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = intent.extras
        var city : City? = null
        if (b != null) city = b.getSerializable(ARG_CITY) as City

        setContent {
            SampleWeatherAppTheme() {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FavWeatherScreen(
                        locationManager = locationManager,
                        requestLocationPermission = ::requestLocationPermission,
                        city = city
                    )
                }
            }
        }
    }
}

@Composable
fun FavWeatherScreen(
    locationManager: LocationManager,
    requestLocationPermission: (onPermissionGranted: () -> Unit) -> Unit,
    city: City?
) {
    WeatherScreen(
        locationManager = locationManager,
        onRequestPermission = requestLocationPermission,
        setBackground = {},
        city = city
    )
}