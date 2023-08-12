package com.sampleweatherapp.viewmodels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sampleweatherapp.models.City
import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import com.sampleweatherapp.network.WeatherRepository
import com.sampleweatherapp.network.WeatherRepositoryImpl
import com.sampleweatherapp.network.response.Response
import com.sampleweatherapp.utilities.PrefUtils
import kotlinx.coroutines.launch
import java.lang.reflect.Type


class WeatherScreenViewModel constructor(private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()) :
    ViewModel() {

    private val _currentWeatherState =
        mutableStateOf<Response<CurrentWeather>>(Response.Success(null))
    private val _forecastWeatherState = mutableStateOf<Response<Forecast>>(Response.Success(null))
    private val gson = Gson()
    private val favourites: MutableList<City> = mutableListOf()

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

    fun saveFav(context: Context, favourites: List<City>) {
        val gsonString = gson.toJson(favourites)
        PrefUtils.with(context).save(PrefUtils.FAV, gsonString)
    }

    fun getFavourites(context: Context): List<City> {
        if (favourites.isNotEmpty()) return favourites
        val favListStr = PrefUtils.with(context).getString(PrefUtils.FAV, "")
        val listOfMyClassObject: Type = object : TypeToken<ArrayList<City?>?>() {}.type
        if (favListStr!!.isNotEmpty()) {
            val favs: List<City> = gson.fromJson(favListStr, listOfMyClassObject)
            favourites.addAll(favs)
        }
        return favourites
    }

    fun addFavourites(context: Context, city: City) {
        if (favourites.isEmpty()) getFavourites(context)
        if (favourites.isNotEmpty()) {
            val index = favourites.indexOfFirst { ob: City ->  ob.cityId == city.cityId}
            if (index != -1) return
        }
        favourites.add(city)
        saveFav(context, favourites)
    }

    fun removeFavourite(context: Context, city: City) {
        val id: Int = favourites.indexOfFirst { target: City -> target.cityId == city.cityId }
        favourites.removeAt(id)
        saveFav(context, favourites)
    }

}