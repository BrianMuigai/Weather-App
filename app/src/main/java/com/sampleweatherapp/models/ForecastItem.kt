package com.sampleweatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ForecastItem(
    @SerializedName("dt") @Expose val dt: Long,
    @SerializedName("main") @Expose val main: Main,
    @SerializedName("weather") @Expose val weather: Weather,
    @SerializedName("wind") @Expose val wind: Wind,
    @SerializedName("visibility") @Expose val visibility: Int,
    @SerializedName("dt_txt") @Expose val dateTimeTExt: String
)