package com.sampleweatherapp.utilities

import com.sampleweatherapp.R
import com.sampleweatherapp.models.ForecastItem
import java.math.BigDecimal
import java.math.RoundingMode


fun getIcon(weatherCondition: String): Int {
    var icon: Int = R.drawable.clear
    when (weatherCondition) {
        WeatherCondition.Sun.name-> icon = R.drawable.clear
        WeatherCondition.Rain.name -> icon = R.drawable.rain
        WeatherCondition.Clouds.name -> icon = R.drawable.partlysunny
    }
    return icon
}

fun tempToInt(temp: Double): Int {
    val bd = BigDecimal(temp)
    val newVal = bd.setScale(0, RoundingMode.CEILING)
    return newVal.toInt()
}

fun cleanForecast(data: List<ForecastItem>) : List<ForecastHolder> {
    val newData : MutableList<ForecastHolder> = mutableListOf()
    val weekDaysSet: MutableSet<String> = mutableSetOf()

    var total = 0.0
    var count = 0
    for (item: ForecastItem in data) {
        val dayOfWeek = getDayOfWeek(item.dt)
        if (weekDaysSet.contains(dayOfWeek)) {
            total += item.main.temp
            count ++
        } else {
            if (total != 0.0) {
                newData.add(
                    ForecastHolder(
                        dayOfWeek = dayOfWeek,
                        icon = getIcon(item.weather.first().description),
                        avgTemp = tempToInt(total / count)
                    )
                )
            }
            total = item.main.temp
            count = 1
            weekDaysSet.add(dayOfWeek)
        }

    }

    return newData
}