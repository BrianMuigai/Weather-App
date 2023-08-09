package com.sampleweatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Forecast(
    @SerializedName("cod") @Expose val cod: String,
    @SerializedName("message") @Expose val message: Int,
    @SerializedName("cnt") @Expose val cnt: Int,
    @SerializedName("list") @Expose val forecasts: List<ForecastItem> = listOf()
)