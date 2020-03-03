package com.example.prety_basic

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.util.EventLog
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.log

class MainActivity : AppCompatActivity(), ShakeEventListener.OnShakeListener, LocationServiceCallBacks {
    private lateinit var locationService: LocationService
    private var mSensorManager: SensorManager? = null
    private var mShake: Sensor? = null;
    private var mShakeListener: ShakeEventListener? = null;
    private var isBound = false;

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            locationService.setCallbacks(this@MainActivity);
            isBound = true
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPanic.setOnClickListener(fun(it: View) {
            locationService.location1?.let { it1 -> this.sendAMessage(it1) };
        })
        this.mShakeListener = ShakeEventListener()
        this.mShakeListener!!.setOnShakeListener(this)
        this.mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        this.mShake = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor;
        this.mSensorManager!!.registerListener(this.mShakeListener as SensorEventListener, mShake, SensorManager.SENSOR_DELAY_NORMAL)
        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent);
        }
    }
    fun sendAMessage(location: Location){
        try {
            var textBody: String = "test panic message, " +
                  "Location: https://www.google.com/maps/dir/?api=1&destination=" + location.latitude + "," + location.longitude +
                "&travelmode=driving"
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS), 101
                )
            }
            Toast.makeText(this@MainActivity, textBody, Toast.LENGTH_LONG).show()
            //val smsManager = SmsManager.getDefault() as SmsManager
            //smsManager.sendTextMessage("<your number here>", null, textBody, null, null)
        }
        catch(e: Exception){
            Log.println(Log.INFO, "sadf", e.message);
        }
    }

    override fun onShake(count: Int) {
            locationService.location1?.let { sendAMessage(it) }
    }

    override fun locationUpdated(location1: Location) {
        sendAMessage(location1)
    }

    override fun askPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101);
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 101);
    }
}
