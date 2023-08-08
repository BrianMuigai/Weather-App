package com.sampleweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sampleweatherapp.ui.WeatherScreen
import com.sampleweatherapp.ui.theme.SampleWeatherAppTheme
import com.sampleweatherapp.utilities.WeatherCondition

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleWeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp(WeatherCondition.SUNNY)
                }
            }
        }
    }
}

@Composable
fun WeatherApp(weatherCondition: WeatherCondition) {
    WeatherScreen(weatherCondition)
}