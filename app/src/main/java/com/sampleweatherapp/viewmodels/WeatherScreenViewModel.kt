package com.sampleweatherapp.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sampleweatherapp.models.City
import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import com.sampleweatherapp.network.WeatherRepository
import com.sampleweatherapp.network.WeatherRepositoryImpl
import com.sampleweatherapp.utilities.Response
import com.sampleweatherapp.utilities.PrefUtils
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.lang.reflect.Type


class WeatherScreenViewModel : ViewModel() {

    private val _currentWeatherState =
        mutableStateOf<Response<CurrentWeather>>(Response.Success(null))
    private val _forecastWeatherState = mutableStateOf<Response<Forecast>>(Response.Success(null))
    private val gson = Gson()
    private val favourites = mutableStateOf<MutableList<City>>(mutableListOf())
    private lateinit var latLng: LatLng
    private lateinit var weatherRepository: WeatherRepository

    val currentWeatherState: State<Response<CurrentWeather>> = _currentWeatherState
    val forecastWeatherState: State<Response<Forecast>> = _forecastWeatherState
    val favouriteState: State<MutableList<City>> = favourites

    fun setContext(context: Context) {
        weatherRepository = WeatherRepositoryImpl( context )
    }
    fun setLatLng(lat: Double, lon: Double) {
        latLng = LatLng(lat, lon)
    }

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

    fun getForecastWeather() {
        viewModelScope.launch {
            weatherRepository.getForecast(latLng.latitude, latLng.longitude).collect { response ->
                _forecastWeatherState.value = response
            }
        }
    }

    fun saveFav(context: Context, favourites: List<City>) {
        val gsonString = gson.toJson(favourites)
        PrefUtils.with(context).save(PrefUtils.FAV, gsonString)
    }

    fun getFavourites(context: Context): List<City> {
        if (favourites.value.isNotEmpty()) favourites.value.clear()
        val favListStr = PrefUtils.with(context).getString(PrefUtils.FAV, "")
        val listOfMyClassObject: Type = object : TypeToken<ArrayList<City?>?>() {}.type
        if (favListStr!!.isNotEmpty()) {
            val favs: List<City> = gson.fromJson(favListStr, listOfMyClassObject)
            favourites.value.addAll(favs)
        }
        return favourites.value
    }

    fun addFavourites(context: Context, city: City) {
        if (favourites.value.isEmpty()) getFavourites(context)
        val index = favourites.value.indexOfFirst { ob: City -> ob.name == city.name }
        if (index == -1) {
            favourites.value.add(city)
            saveFav(context, favourites.value)
        }
    }

    fun removeFavourite(context: Context, city: City) {
        val id: Int = favouriteState.value.indexOfFirst { target: City -> target.name == city.name }
        if (id != -1) {
            favouriteState.value.removeAt(id)
            saveFav(context, favouriteState.value)
        }
    }

}