package com.sampleweatherapp

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.FirebaseApp
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.google.firebase.appdistribution.FirebaseAppDistributionException
import com.sampleweatherapp.ui.MainScreen
import com.sampleweatherapp.ui.theme.SampleWeatherAppTheme
import com.sampleweatherapp.utilities.LocationManager


class MainActivity : ComponentActivity() {
    private var onPermissionGranted: (() -> Unit)? = null
    private lateinit var placeClient: PlacesClient
    private lateinit var geocoder: Geocoder
    private lateinit var locationManager: LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Places.initialize(this.applicationContext, BuildConfig.MAPS_API_KEY)
        FirebaseApp.initializeApp(this)
        locationManager = LocationManager(this)
        locationManager.startLocationTracking()
        placeClient = Places.createClient(this)
        geocoder = Geocoder(this)
        setContent {
            SampleWeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp(
                        requestLocationPermission = ::requestLocationPermission,
                        locationManager = locationManager,
                        placesClient = placeClient,
                        geocoder = geocoder
                    )
                }
            }
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(
                            this,
                            getString(R.string.permission_granted),
                            Toast.LENGTH_SHORT
                        ).show()
                        onPermissionGranted!!.invoke()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }

    private fun requestLocationPermission(permissionGrantedCallback: () -> Unit) {
        setOnPermissionGrantedCallback(permissionGrantedCallback)
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION), 1
            )
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION), 1
            )
        }
    }

    private fun setOnPermissionGrantedCallback(callback: () -> Unit) {
        onPermissionGranted = callback
    }

    override fun onResume() {
        super.onResume()
        val firebaseAppDistribution = FirebaseAppDistribution.getInstance()
        firebaseAppDistribution.updateIfNewReleaseAvailable()
            .addOnProgressListener { }
            .addOnFailureListener { e: Exception ->
                // (Optional) Handle errors.
                if (e is FirebaseAppDistributionException) {
                    e.printStackTrace()
                    when (e.errorCode) {
                        FirebaseAppDistributionException.Status.NOT_IMPLEMENTED -> {}
                        else -> {}
                    }
                }
            }
    }
}

@Composable
fun WeatherApp(
    locationManager: LocationManager,
    requestLocationPermission: (onPermissionGranted: () -> Unit) -> Unit,
    placesClient: PlacesClient, geocoder: Geocoder
) {
    MainScreen(
        locationManager = locationManager,
        onRequestPermission = requestLocationPermission,
        placesClient = placesClient,
        geocoder = geocoder
    )
}