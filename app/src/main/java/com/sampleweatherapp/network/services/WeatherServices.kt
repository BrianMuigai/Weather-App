package com.sampleweatherapp.network.services

import android.content.Context
import com.sampleweatherapp.BuildConfig
import com.sampleweatherapp.models.CurrentWeather
import com.sampleweatherapp.models.Forecast
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServices {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("appid") key: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ) : CurrentWeather

    @GET("forecast")
    suspend fun getForecastWeather(
        @Query("appid") key: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ) : Forecast

    companion object {
        private var retrofitService: WeatherServices? = null
        var cacheSize = 10 * 1024 * 1024 // 10 MB

        fun getInstance(context: Context) : WeatherServices {
            if (retrofitService == null) {
                val cache = Cache(context.cacheDir, cacheSize.toLong())
                val client = OkHttpClient
                    .Builder()
                    .cache(cache)
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