package com.sampleweatherapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.sampleweatherapp.utilities.WeatherCondition
import com.sampleweatherapp.viewmodels.WeatherScreenViewModel

@Preview(showSystemUi = true)
@Composable
fun WeatherScreen(weatherViewModel: WeatherScreenViewModel = viewModel()) {
    fun launch() {
        weatherViewModel.getCurrentWeather(1.2921, 36.8219)
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
                }
            }

            is Response.Failure -> {
                ErrorScreen(errorState = ErrorState.ERROR, onClickRetry = { launch() })
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
                            when (val forecastResponse =
                                weatherViewModel.forecastWeatherState.value) {
                                is Response.Loading -> {
                                    Box(Modifier.weight(1f).fillMaxSize()) {
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
                                        Box(modifier = Modifier
                                            .padding(all = 16.dp)
                                            .fillMaxSize()) {
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
            Text(text = currentWeather.main.tempMin.toString() + "\u00B0")
            Text(text = stringResource(R.string.min))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = currentWeather.main.temp.toString() + "\u00B0")
            Text(text = stringResource(R.string.current))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), horizontalAlignment = Alignment.End
        ) {
            Text(text = currentWeather.main.tempMax.toString() + "\u00B0")
            Text(text = stringResource(R.string.max))
        }
    }
}

@Composable
fun RestOfWeek(days: List<ForecastItem>) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(days) { item: ForecastItem -> ListItem(item) }
    }
}

@Composable
fun ListItem(forecast: ForecastItem, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        Text(text = forecast.main.temp.toString())
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