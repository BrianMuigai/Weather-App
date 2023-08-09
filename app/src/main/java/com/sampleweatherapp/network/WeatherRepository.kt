package com.sampleweatherapp.network

import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import com.sampleweatherapp.network.response.Response
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double) : Flow<Response<CurrentWeather>>
    fun getForecast(lat: Double, lon:Double): Flow<Response<Forecast>>
}