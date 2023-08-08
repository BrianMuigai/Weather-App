package com.sampleweatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coordinates(
    @SerializedName("lon")
    @Expose var lon: Double, @SerializedName("lat")
    @Expose var lat: Double
) : Serializable