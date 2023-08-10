package com.sampleweatherapp.utilities
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

fun getDayOfWeek(timeStamp: Long): String {
    val dt = Instant.ofEpochSecond(timeStamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    val dayOfWeek : String = dt.dayOfWeek.name
    val capitalize = dayOfWeek.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString() }
    return capitalize
}