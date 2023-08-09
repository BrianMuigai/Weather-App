package com.sampleweatherapp.network

import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import com.sampleweatherapp.network.response.Response
import com.sampleweatherapp.network.services.WeatherServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepositoryImpl constructor(private val weatherServices: WeatherServices = WeatherServices.getInstance()) :
    WeatherRepository {
    override fun getCurrentWeather(lat: Double, lon: Double): Flow<Response<CurrentWeather>> = flow{
        try{
            emit(Response.Loading)
            val responseApi = weatherServices.getCurrentWeather(lat=lat, lon = lon)
            emit(Response.Success(responseApi))
        }catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getForecast(lat: Double, lon: Double): Flow<Response<Forecast>>  = flow{
        try{
            emit(Response.Loading)
            val responseApi = weatherServices.getForecastWeather(lat=lat, lon = lon)
            emit(Response.Success(responseApi))
        }catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)


}