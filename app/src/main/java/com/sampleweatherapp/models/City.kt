package com.sampleweatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
    @SerializedName("id") @Expose val cityId: Long,
    @SerializedName("name") @Expose val name: String,
    @SerializedName("coord") @Expose val coordinates: Coordinates,
    @SerializedName("country") @Expose val country: String
) : Serializable