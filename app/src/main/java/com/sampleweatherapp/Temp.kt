package com.sampleweatherapp
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.sampleweatherapp.models.Weather
//import com.sampleweatherapp.utilities.WeatherCondition
//
//@Preview
//@Composable
//fun WeatherMain(weatherCondition: WeatherCondition = WeatherCondition.SUNNY) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//
//        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
//            Box(
//                modifier = Modifier
//                    .wrapContentHeight()
//                    .fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .matchParentSize(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    ReusableImage(
//                        image =
//                        when (weatherCondition) {
//                            WeatherCondition.SUNNY -> {
//                                R.drawable.forest_sunny
//                            }
//                            WeatherCondition.CLOUDY -> {
//                                R.drawable.forest_cloudy
//                            }
//                            else -> {
//                                R.drawable.forest_rainy
//                            }
//                        },
//                        contentScale = ContentScale.FillWidth,
//                        contentDesc = "Background",
//                        modifier = Modifier
//                            .size(64.dp)
//                            .padding(bottom = 4.dp)
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    ReusableImage(
//                        image = R.drawable.ic_cloud_zappy,
//                        contentScale = ContentScale.Fit,
//                        contentDesc = "Weather img",
//                        modifier = Modifier
//                            .size(250.dp)
//                            .padding(top = 40.dp, bottom = 20.dp)
//                    )
//                    Text(text = Weather().condition, style = MaterialTheme.typography.headlineMedium)
//                    Spacer(modifier = Modifier.size(4.dp))
//                    Row(modifier = Modifier.wrapContentSize()) {
//                        Text(
//                            text = Weather().update.first().temp,
//                            style = MaterialTheme.typography.headlineLarge,
//                            modifier = Modifier.offset(y = (-24).dp)
//                        )
//                        ReusableImage(
//                            image = R.drawable.ic_degree,
//                            contentScale = ContentScale.Fit,
//                            contentDesc = "Degree Icon",
//                            modifier = Modifier
//                        )
//                    }
//                    Text(
//                        text = Weather().description,
//                        style = MaterialTheme.typography.labelMedium,
//                        textAlign = TextAlign.Center
//                    )
//                    Spacer(modifier = Modifier.size(16.dp))
//                }
//            }
//
//            DailyWeatherList()
//        }
//    }
//}
//
//@Composable
//fun ReusableImage(image: Int, contentScale: ContentScale, contentDesc: String, modifier: Modifier) {
//    val paintImage: Painter = painterResource(id = image)
//    Image(
//        painter = paintImage,
//        contentDescription = contentDesc,
//        contentScale = contentScale,
//        modifier = modifier
//    )
//}
//
//@Composable
//fun DailyWeatherList() {
//    LazyRow(
//        content = {
//            items(Weather().update) { weather ->
//                DailyWeatherItem(weather)
//            }
//        }
//    )
//}
//
//@Composable
//fun DailyWeatherItem(weatherUpdate: Weather.WeatherUpdate) {
//    Card(
//        modifier = Modifier
//            .wrapContentSize()
//            .padding(4.dp),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 1.dp
//        ),
//        shape = MaterialTheme.shapes.small
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
//        ) {
//            ReusableImage(
//                image = when (weatherUpdate.icon) {
//                    "wind" -> R.drawable.ic_moon_cloud_fast_wind
//                    "rain" -> R.drawable.ic_moon_cloud_mid_rain
//                    "angledRain" -> R.drawable.ic_sun_cloud_angled_rain
//                    "thunder" -> R.drawable.ic_zaps
//                    else -> R.drawable.ic_moon_cloud_fast_wind
//                },
//                contentScale = ContentScale.Fit,
//                contentDesc = "Weather Icon",
//                modifier = Modifier
//                    .size(64.dp)
//                    .padding(bottom = 4.dp)
//            )
//            Text(
//                text = weatherUpdate.time,
////                style = MaterialTheme.typography.caption,
//                modifier = Modifier.padding(4.dp)
//            )
//            Text(
//                text = "${weatherUpdate.temp}°",
//                style = MaterialTheme.typography.labelMedium,
//                modifier = Modifier.padding(4.dp)
//            )
//        }
//    }
//}