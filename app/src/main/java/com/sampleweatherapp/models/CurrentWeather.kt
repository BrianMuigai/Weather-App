package com.sampleweatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("id") @Expose val id: Long,
    @SerializedName("name") @Expose val name: String,
    @SerializedName("cod") @Expose val cod: Int,
    @SerializedName("coord") @Expose val coord: Coordinates,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("base") @Expose val base: String,
    @SerializedName("main") @Expose val main: Main,
    @SerializedName("visibility") @Expose val visibility: Int,
    @SerializedName("wind") @Expose val wind: Wind,
    @SerializedName("dt") @Expose val dt: Long
)