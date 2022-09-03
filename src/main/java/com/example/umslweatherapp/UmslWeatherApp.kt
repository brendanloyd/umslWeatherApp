package com.example.umslweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.kotlindemos.PermissionUtils
import com.example.kotlindemos.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.kotlindemos.PermissionUtils.isPermissionGranted
import com.example.umslweatherapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UmslWeatherApp : AppCompatActivity(), OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnMapReadyCallback,
    OnRequestPermissionsResultCallback {
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private lateinit var binding: ActivityMapsBinding
    private var permissionDenied = false
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMapsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView", e)
            throw e
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // [START maps_check_location_permission]
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            // Get LocationManager object from System Service LOCATION_SERVICE
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            // Create a criteria object to retrieve provider
            val criteria = Criteria()

            // Get the name of the best provider
            val provider = locationManager.getBestProvider(criteria, true)

            // Get Current Location
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                setUpMap(location.latitude, location.longitude)
            } else {
                //setUpMap(42.72703748599169, -94.22678956125796)
                setUpMap(38.712324129625685, -90.31128335352115)
            }

            return
        }

        // 2. If a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
        // [END maps_check_location_permission]
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        enableMyLocation()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return true
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
            // [END_EXCLUDE]
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap(lat: Double, long: Double) {
        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker").snippet("Snippet"))

        val position = CameraUpdateFactory.newLatLng(LatLng(lat, long))
        map.animateCamera(position)

        val zoom = CameraUpdateFactory.zoomTo(15f)
        map.animateCamera(zoom)

        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        val latLng = LatLng(lat, long)

        // Show the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(14f))
        map.addMarker(
            MarkerOptions().position(LatLng(lat, long)).title("You are here!")
                .snippet("Consider yourself located")
        )

        fetchWeather(lat, long)
    }

    private fun fetchWeather(lat: Double, long: Double) {
        val request = ServiceBuilder.buildService(WeatherEndpoints::class.java)
        val call = request.getWeather(
            lat,
            long,
            getString(R.string.api_key),
            getString(R.string.temp_unit)
        )

        val currentTemp: TextView = findViewById(R.id.temp)
        val description: TextView = findViewById(R.id.maxTemp)
        val humidity: TextView = findViewById(R.id.minTemp)

        call.enqueue(object : Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {

                    currentTemp.text = "Temperature: " + response.body()?.main?.temp.toString()
                    description.text = "Description:  " + response.body()?.weather?.get(0)?.main.toString()
                    humidity.text = "Humidity: " + response.body()?.main?.humidity.toString()

                    val channelId = "CHANNEL 1"
                    val channelName = "Weather Notification"

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(channelId, channelName , NotificationManager.IMPORTANCE_DEFAULT).apply {
                            lightColor = Color.BLUE
                            enableLights(true)

                        }
                        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        manager.createNotificationChannel(channel)
                    }

                    if(description.text.contains("Rain", true)) {
                        val builder =
                            NotificationCompat.Builder(this@UmslWeatherApp, channelId)
                                .setSmallIcon(R.drawable.ic_baseline_umbrella)
                                .setContentTitle("Rain Expected.")
                                .setContentText("Carry an umbrella!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)
                                .build()

                            val manager = NotificationManagerCompat.from(this@UmslWeatherApp)
                            manager.notify(1, builder)
                    }
                    if(description.text.contains("Cloud", true)) {
                        val builder =
                            NotificationCompat.Builder(this@UmslWeatherApp,channelId)
                                .setSmallIcon(R.drawable.ic_baseline_umbrella)
                                .setContentTitle("It's a little cloudy today.")
                                .setContentText("Don't let it upset you!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .build()

                        val manager = NotificationManagerCompat.from(this@UmslWeatherApp)
                        manager.notify(1, builder)
                    }

//                    getImage("${response.body()?.weather?.get(0)?.icon}@2x.png")

                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@UmslWeatherApp, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun getImage(imgPath: String) {
//        val icon: ImageView = findViewById(R.id.icon)
//
//        val request = ServiceBuilder.buildService(WeatherEndpoints::class.java)
//
//        val call = request.getIcon(imgPath, getString(R.string.api_key))
//
//        call.enqueue(object : Callback<Icon> {
//            override fun onResponse(
//                call: Call<WeatherResponse>,
//                response: Response<WeatherResponse>
//            ) {
//                if (response.isSuccessful) {
//                    icon.setImageResource(response.body())
//                }
//            }
//
//
//            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
//                Toast.makeText(this@MapsActivity, "${t.message}", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        })
//    }
}