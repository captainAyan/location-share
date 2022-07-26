package com.github.captainayan.locationshare.android

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class LocationService: Service() {

    private val TAG: String = "LOCATION_SERV"

    private val NOTIFICATION_CHANNEL_ID: String = "location_service_channel"
    private val NOTIFICATION_CHANNEL_NAME: String = "location_service_channel"
    private val NOTIFICATION_ID: Int = 1
    private val NOTIFICATION_CONTENT_TITLE: String = "Sharing Location"
    private val NOTIFICATION_CONTENT_TEXT: String = "Your location is currently being shared"
    private val LOCATION_UPDATE_DELAY: Long = 30000
    private val LOCATION_UPDATE_DELAY_FASTEST: Long = 2000

    private var locationSender: LocationSender? = null
    private var uuid: String? = null

    private var executor: ThreadPoolExecutor? = null

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    companion object {
        var IS_SERVICE_RUNNING = false

        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, LocationService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, LocationService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        IS_SERVICE_RUNNING = true

        val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
        executor = ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES,
            1L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue()
        )

        locationRequest = LocationRequest.create();
        locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = LOCATION_UPDATE_DELAY
        locationRequest.fastestInterval = LOCATION_UPDATE_DELAY_FASTEST

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onDestroy() // cancels the service
        }

        fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(this@LocationService)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                if (locationResult.locations.size > 0) {
                    val index = locationResult.locations.size - 1
                    val latitude = locationResult.locations[index].latitude
                    val longitude = locationResult.locations[index].longitude
                    Log.e(TAG, "Location : Latitude: $latitude | Longitude: $longitude")

                    GlobalScope.launch{
                        locationSender?.sendLocation(uuid!!, longitude, latitude)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /**
         * Sample app
         *
         * @link https://github.com/android/location-samples/tree/432d3b72b8c058f220416958b444274ddd186abd/LocationUpdatesForegroundService
         *
         * */

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(NOTIFICATION_CONTENT_TITLE)
            .setContentText(NOTIFICATION_CONTENT_TEXT)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        locationSender = LocationSender.getInstance()
        uuid = PreferenceManager.getDefaultSharedPreferences(this).getString("uuid", "").toString()

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        /*
        executor?.execute {
                try {
                    while (IS_SERVICE_RUNNING) {
                        GlobalScope.launch {

                            LocationServices.getFusedLocationProviderClient(this@LocationService)
                            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)

                                    LocationServices.getFusedLocationProviderClient(this@LocationService)
                                        .removeLocationUpdates(this)

                                    if (locationResult.locations.size > 0) {
                                        val index = locationResult.locations.size - 1
                                        val latitude = locationResult.locations[index].latitude
                                        val longitude = locationResult.locations[index].longitude
                                        Log.e(TAG, "Location : Latitude: $latitude | Longitude: $longitude", )
                                        Toast.makeText(this@LocationService, "Location : Latitude: $latitude | Longitude: $longitude", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }, null)


                            if (locationGPS != null) {
                            locationSender?.sendLocation(uuid!!, locationGPS.longitude, locationGPS.latitude)
                        }
                        else {
                            Log.e(TAG, "onStartCommand: locationGPS is null", )
                        }
                        }
                        Log.e(TAG, "SENDING REQUEST")
                        Thread.sleep(10000)
                    }

                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        */

        Log.e(TAG, "NOTIFICATION POSTED")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        IS_SERVICE_RUNNING = false

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

}