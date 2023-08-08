package com.sampleweatherapp.network

import com.sampleweatherapp.BuildConfig
import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherServices {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("appid") key: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
        @Query("lat") page: Int,
        @Query("lon") pageSize: Int,
    ) : CurrentWeather

    @GET("forecast")
    suspend fun getGamesDetail(
        @Query("appid") key: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
        @Query("lat") page: Int,
        @Query("lon") pageSize: Int,
    ) : Forecast

    companion object {
        private var retrofitService: WeatherServices? = null
        fun getInstance() : WeatherServices {
            if (retrofitService == null) {
                val client = OkHttpClient
                    .Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.API_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                retrofitService = retrofit.create(WeatherServices::class.java)
            }
            return retrofitService!!
        }
    }
}