package com.sampleweatherapp.utilities

enum class WeatherCondition {
    Clouds,
    Rain,
    Sun
}

enum class ErrorState {
    SOMETHING_WENT_WRONG,
    ERROR
}

enum class Screen {
    FAVOURITES,
    PICK_LOCATION,
    FAVOURITE_LOCATION_WEATHER
}

val ARG_CITY = "city"