package com.laurens.utils

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import java.io.IOException
import java.util.*

class GPSTracker(private val context: Context) : Service(), LocationListener {

    var isGPSEnabled = false
        private set
    var isNetworkEnabled = false
        private set
    var isGPSTrackingEnabled = false
        private set
    var location: Location? = null
        private set
    var latitude = 0.0
        private set
    var longitude = 0.0
        private set
    var geocoderMaxResults = 1

    private var locationManager: LocationManager? = null
    private var providerInfo: String? = null

    @SuppressLint("MissingPermission")
    fun getLocation() {
        try {
            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            when {
                isGPSEnabled -> {
                    isGPSTrackingEnabled = true
                    Log.d(TAG, "Application uses GPS Service")
                    providerInfo = LocationManager.GPS_PROVIDER
                }
                isNetworkEnabled -> {
                    isGPSTrackingEnabled = true
                    Log.d(TAG, "Application uses Network State to get GPS coordinates")
                    providerInfo = LocationManager.NETWORK_PROVIDER
                }
            }

            if (!providerInfo.isNullOrEmpty()) {
                locationManager!!.requestLocationUpdates(
                    providerInfo!!,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                location = locationManager!!.getLastKnownLocation(providerInfo!!)
                updateGPSCoordinates()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Impossible to connect to LocationManager", e)
        }
    }

    private fun updateGPSCoordinates() {
        location?.let {
            latitude = it.latitude
            longitude = it.longitude
        }
    }

    fun fetchLatitude(): Double {
        location?.let {
            latitude = it.latitude
        }
        return latitude
    }

    fun fetchLongitude(): Double {
        location?.let {
            longitude = it.longitude
        }
        return longitude
    }

    fun stopUsingGPS() {
        locationManager?.removeUpdates(this@GPSTracker)
    }


    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("GPS Settings")
        alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?")

        alertDialog.setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }


    private fun getGeocoderAddress(context: Context?): List<Address>? {
        if (context == null || location == null) {
            Log.e(TAG, "Context or location is null")
            return null
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
        } catch (e: IOException) {
            Log.e(TAG, "Impossible to connect to Geocoder", e)
            null
        }
    }

    fun getAddressLine(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            null
        }
    }

    fun getLocality(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].locality
        } else {
            null
        }
    }

    fun getPostalCode(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].postalCode
        } else {
            null
        }
    }

    fun getCountryName(context: Context?): String? {
        val addresses = getGeocoderAddress(context)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].countryName
        } else {
            null
        }
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        updateGPSCoordinates()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private val TAG = GPSTracker::class.java.name
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }

    init {
        getLocation()
    }

}