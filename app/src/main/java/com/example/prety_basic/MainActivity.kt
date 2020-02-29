package com.example.prety_basic

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.EventLog
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), ShakeEventListener.OnShakeListener {

    private var mSensorManager: SensorManager? = null
    private var mShake: Sensor? = null;
    private var mShakeListener: ShakeEventListener? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPanic.setOnClickListener(fun(it: View) {
            this.sendAMessage()
        })
        this.mShakeListener = ShakeEventListener()
        this.mShakeListener!!.setOnShakeListener(this)
        this.mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        this.mShake = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor;
        this.mSensorManager!!.registerListener(this.mShakeListener as SensorEventListener, mShake, SensorManager.SENSOR_DELAY_NORMAL)
    }
    fun sendAMessage(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.SEND_SMS), 101);
        Toast.makeText(this@MainActivity, "button clicked", Toast.LENGTH_LONG).show()
        val smsManager = SmsManager.getDefault() as SmsManager
        smsManager.sendTextMessage("+919964375269", null, "test panic message", null, null)
    }

    override fun onShake(count: Int) {
        sendAMessage()
    }
}
