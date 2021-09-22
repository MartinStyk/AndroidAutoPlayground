package com.example.androidautoplayground

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

const val CHANNEL_ID = "notifChannel"
const val NOTIF_ID = 987

class LocationService : Service(), LocationListener {

    private val locationManager by lazy { applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    override fun onCreate() {
        Log.e("XXX", "onCreate service")
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_NONE)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("My Awesome App")
            .setContentText("Updating location...").build()

        Log.e("XXX", "onCreate service - startForeground")
        startForeground(NOTIF_ID, notification)

        startLocationUpdates()
    }

    override fun onDestroy() {
        Log.e("XXX", "onDestroy service")
        super.onDestroy()
        removeLocationUpdates()
    }

    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    private fun removeLocationUpdates() {
        locationManager.removeUpdates(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return Binder("binder")
    }

    override fun onLocationChanged(location: Location) {
        Log.e("XXX", "onLocation changed by " + location.provider + " location" + location)
    }


    override fun onProviderDisabled(provider: String) {
        Log.e("XXX", "onProviderDisabled " + provider)
    }

    override fun onProviderEnabled(provider: String) {
        Log.e("XXX", "onProviderEnabled " + provider)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.e("XXX", "onStatusChanged " + status)
    }

}