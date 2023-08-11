package com.sampleweatherapp.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sampleweatherapp.R
import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import com.sampleweatherapp.network.WeatherRepository
import com.sampleweatherapp.network.WeatherRepositoryImpl
import com.sampleweatherapp.network.response.Response
import kotlinx.coroutines.launch
import java.lang.Exception

class WeatherScreenViewModel constructor(private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()) :
    ViewModel() {

    private val _currentWeatherState =
        mutableStateOf<Response<CurrentWeather>>(Response.Success(null))
    private val _forecastWeatherState = mutableStateOf<Response<Forecast>>(Response.Success(null))

    val currentWeatherState: State<Response<CurrentWeather>> = _currentWeatherState
    val forecastWeatherState: State<Response<Forecast>> = _forecastWeatherState

    fun setFailCurrentWeatherResponse(message: String) {
        _currentWeatherState.value =
            Response.Failure(Exception(message))
    }

    fun getCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(lat, lon).collect { response ->
                _currentWeatherState.value = response
            }
        }
    }

    fun getForecastWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.getForecast(lat, lon).collect { response ->
                _forecastWeatherState.value = response
            }
        }
    }

}