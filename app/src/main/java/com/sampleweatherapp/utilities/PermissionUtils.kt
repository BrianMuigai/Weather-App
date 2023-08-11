package com.sampleweatherapp.utilities

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import androidx.core.app.ActivityCompat

fun hasLocationPermission(context: Context): Boolean {
    if (ActivityCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            ACCESS_COARSE_LOCATION
        ) != PERMISSION_GRANTED
    ) {
        return false
    }
    return true
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}