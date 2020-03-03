package com.example.prety_basic

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat


interface LocationServiceCallBacks {
    fun locationUpdated(location1: Location)
    fun askPermission();
}

class LocationService() : Service(), LocationListener {
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location1:Location? = null
    protected var locationManager: LocationManager? = null
    private val binder = LocalBinder()
    private var locationServiceCallBacks: LocationServiceCallBacks? = null

    fun setCallbacks(callbacks: LocationServiceCallBacks) {
        locationServiceCallBacks = callbacks
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        try {
            this.locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            this.isGPSEnabled = this.locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            this.isNetworkEnabled = this.locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!this.isGPSEnabled && !this.isNetworkEnabled) {
            } else {
                this.canGetLocation = true
                if (this.isGPSEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationServiceCallBacks?.askPermission();
                    }
                    if (this.location1 == null) {
                        this.locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                           com.example.prety_basic.LocationService.Companion.MIN_TIME_BW_UPDATES,
                           com.example.prety_basic.LocationService.Companion.MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                           this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (this.locationManager != null) {
                            this.location1 = this.locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        }
                    }
                }
                //get location from network if this is available
                else if (this.isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                      locationServiceCallBacks?.askPermission();
                    }
                    this.locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        com.example.prety_basic.LocationService.Companion.MIN_TIME_BW_UPDATES,
                        com.example.prety_basic.LocationService.Companion.MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    Log.d("Network", "Network")
                    if (this.locationManager != null) {
                        this.location1 = this.locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_NOT_STICKY;
    }
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LocationService = this@LocationService
    }

    override fun onLocationChanged(location1: Location) {
        locationServiceCallBacks?.locationUpdated(location1);
    }
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
    }
    override fun onBind(arg0: Intent): IBinder? {
        return binder;
    }
    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters
        // The minimum time between updates in milliseconds
        const val MIN_TIME_BW_UPDATES = 500 // 1 minute
            .toLong()
    }
}