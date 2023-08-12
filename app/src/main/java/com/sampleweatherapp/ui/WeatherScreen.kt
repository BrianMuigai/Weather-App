package com.sampleweatherapp.ui

import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sampleweatherapp.R
import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.ForecastItem
import com.sampleweatherapp.network.response.Response
import com.sampleweatherapp.ui.components.ErrorScreen
import com.sampleweatherapp.ui.components.LoadingAnimation
import com.sampleweatherapp.ui.theme.cloudy
import com.sampleweatherapp.ui.theme.rainy
import com.sampleweatherapp.ui.theme.sunny
import com.sampleweatherapp.utilities.ErrorState
import com.sampleweatherapp.utilities.ForecastHolder
import com.sampleweatherapp.utilities.LocationManager
import com.sampleweatherapp.utilities.WeatherCondition
import com.sampleweatherapp.utilities.cleanForecast
import com.sampleweatherapp.utilities.hasLocationPermission
import com.sampleweatherapp.utilities.isLocationEnabled
import com.sampleweatherapp.utilities.tempToInt
import com.sampleweatherapp.viewmodels.WeatherScreenViewModel

@Composable
fun WeatherScreen(
    locationManager: LocationManager,
    weatherViewModel: WeatherScreenViewModel = viewModel(),
    onRequestPermission: (onPermissionGranted: () -> Unit) -> Unit,
    setBackground: (color: Color) -> Unit
) {
    val context = LocalContext.current

    fun launch() {
        if (hasLocationPermission(context)) {
            if (isLocationEnabled(context)) {
                locationManager.getLastLocation(onSuccess = { lat: Double, lon: Double ->
                    weatherViewModel.getCurrentWeather(lat, lon)
                })
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.turn_on_location),
                    Toast.LENGTH_LONG
                ).show()
                weatherViewModel.setFailCurrentWeatherResponse(
                    message = context.getString(R.string.missing_permission)
                )
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            weatherViewModel.setFailCurrentWeatherResponse(
                message = context.getString(R.string.missing_permission)
            )
            onRequestPermission { launch() }
        }
    }


    launch()

    Surface {

        when (val currentWeatherResponse = weatherViewModel.currentWeatherState.value) {
            is Response.Loading -> {
                LoadingAnimation()
            }

            is Response.Success -> {
                if (currentWeatherResponse.data == null) {
                    ErrorScreen(onClickRetry = { launch() })
                } else {
                    Screen(
                        currentWeather = currentWeatherResponse.data,
                        weatherViewModel = weatherViewModel
                    )
                    setBackground.invoke(
                        when (currentWeatherResponse.data.weather[0].main) {
                            WeatherCondition.Sun.name -> sunny
                            WeatherCondition.Clouds.name -> cloudy
                            else -> rainy
                        }
                    )
                }
            }

            is Response.Failure -> {
                ErrorScreen(
                    errorState = ErrorState.ERROR,
                    onClickRetry = { launch() },
                    title = currentWeatherResponse.e!!.message.toString()
                )
            }
        }
    }

}

@Composable
fun Screen(currentWeather: CurrentWeather, weatherViewModel: WeatherScreenViewModel) {
    fun launch() {
        weatherViewModel.getForecastWeather(1.2921, 36.8219)
    }
    launch()

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Header(
                        currentWeather = currentWeather,
                        containerHeight = this@BoxWithConstraints.maxHeight,
                        image = when (currentWeather.weather.first().main) {
                            WeatherCondition.Sun.name -> R.drawable.forest_sunny
                            WeatherCondition.Clouds.name -> R.drawable.forest_cloudy
                            else -> R.drawable.forest_rainy
                        }
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (currentWeather.weather[0].main) {
                                    WeatherCondition.Sun.name -> sunny
                                    WeatherCondition.Clouds.name -> cloudy
                                    else -> rainy
                                }
                            )
                            .heightIn(this@BoxWithConstraints.maxHeight)
                            .fillMaxWidth()
                    ) {
                        Column {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                MinCurrentMax(currentWeather = currentWeather)
                            }
                            Divider(thickness = 1.dp, color = Color.White)
                            Spacer(modifier = Modifier.padding(vertical = 20.dp))
                            when (val forecastResponse =
                                weatherViewModel.forecastWeatherState.value) {
                                is Response.Loading -> {
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LoadingAnimation()
                                    }
                                }

                                is Response.Success -> {
                                    if (forecastResponse.data == null) {
                                        Log.e("WeatherScreen", "returned a null weather forecast")
                                        Box(Modifier.weight(1f)) {
                                            ErrorScreen(onClickRetry = { launch() })
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .fillMaxSize()
                                        ) {
                                            RestOfWeek(days = forecastResponse.data.forecasts)
                                        }
                                    }
                                }

                                is Response.Failure -> {
                                    Box(Modifier.weight(1f)) {
                                        ErrorScreen(
                                            errorState = ErrorState.ERROR,
                                            onClickRetry = { launch() })
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header(currentWeather: CurrentWeather, containerHeight: Dp, image: Int) {
    Box(contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier
                .heightIn(max = containerHeight / 2)
                .fillMaxWidth(),
            painter = painterResource(id = image),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.wrapContentSize()) {
                Text(
                    text = currentWeather.main.temp.toString(),
                    style = MaterialTheme.typography.headlineLarge
                )
                ReusableImage(
                    image = R.drawable.ic_degree,
                    contentScale = ContentScale.Fit,
                    contentDesc = "Degree Icon",
                    modifier = Modifier
                )
            }
            Text(
                text = currentWeather.weather.first().main,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun MinCurrentMax(currentWeather: CurrentWeather) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = tempToInt(currentWeather.main.tempMin).toString() + "\u00B0")
            Text(text = stringResource(R.string.min))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = tempToInt(currentWeather.main.temp).toString() + "\u00B0")
            Text(text = stringResource(R.string.current))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), horizontalAlignment = Alignment.End
        ) {
            Text(text = tempToInt(currentWeather.main.tempMax).toString() + "\u00B0")
            Text(text = stringResource(R.string.max))
        }
    }
}

@Composable
fun RestOfWeek(days: List<ForecastItem>) {
    val cleanedData: List<ForecastHolder> = cleanForecast(days)
    LazyColumn(Modifier.fillMaxSize()) {
        items(cleanedData) { item: ForecastHolder -> ListItem(item) }
    }
}

@Composable
fun ListItem(forecast: ForecastHolder, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(modifier.fillMaxWidth()) {
            Text(
                text = forecast.dayOfWeek, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            ReusableImage(
                image = forecast.icon, contentScale = ContentScale.Fit,
                contentDesc = "Degree Icon",
                modifier = Modifier
                    .width(width = 30.dp)
                    .height(height = 30.dp)
            )
            Text(
                text = forecast.avgTemp.toString() + "°", modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun ReusableImage(image: Int, contentScale: ContentScale, contentDesc: String, modifier: Modifier) {
    val paintImage: Painter = painterResource(id = image)
    Image(
        painter = paintImage,
        contentDescription = contentDesc,
        contentScale = contentScale,
        modifier = modifier
    )
}