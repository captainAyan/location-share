package com.github.captainayan.locationshare.android

import Utility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.captainayan.locationshare.android.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: MaterialToolbar

    private val LOCATION_NOISE_THRESHOLD: Double = 0.0004 // IMPORTANT tune the threshold

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var polylineOptions: PolylineOptions = PolylineOptions()
    private lateinit var currentMarker:Marker;
    private var zoom:Float = 15.0f

    private lateinit var uuid: String
    private var url: String = BuildConfig.SERVER_BASE_URL+"/?uuid="
    private var mSocket: Socket? = null

    private lateinit var longitudeTv: TextView
    private lateinit var latitudeTv: TextView
    private lateinit var lastUpdateTv: TextView
    private lateinit var watcherTv: TextView
    private lateinit var stopTrackingBtn: Button

    private val sdf = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        uuid = intent.getStringExtra("uuid").toString()
        url += uuid

        toolbar = findViewById<View>(R.id.topAppBar) as MaterialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { this@MapsActivity.finish() }

        longitudeTv = findViewById<TextView>(R.id.longitudeTv)
        latitudeTv = findViewById<TextView>(R.id.latitudeTv)
        lastUpdateTv = findViewById<TextView>(R.id.lastUpdateTv)
        watcherTv = findViewById<TextView>(R.id.watcherTv)

        stopTrackingBtn = findViewById<Button>(R.id.stopTrackingBtn)

        stopTrackingBtn.setOnClickListener {
            finish()
        }

        try {
            mSocket = IO.socket(url)
            mSocket?.connect()
            mSocket?.on("chat message") {latLng ->

                val json:JSONObject = JSONObject(latLng[0].toString())

                val lng:Double = json.get("lng").toString().toDouble()
                val lat:Double = json.get("lat").toString().toDouble()
                this.newLocation(lat, lng)

            }
        } catch (e: URISyntaxException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            throw RuntimeException(e)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));

        mMap.setMinZoomPreference(1.0f)
        mMap.setMaxZoomPreference(20.0f)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.disconnect()
    }

    private fun newLocation (lat:Double, lng:Double) {
        Log.e("MAP ACTIVITY", "onCreate: GOT LOCATION")

        val location = LatLng(lat, lng)

        runOnUiThread {
            if(polylineOptions.points.size != 0) { // everyone after the first one
                val distance: Double = Utility.getLocationDifference(currentMarker.position.latitude,
                    currentMarker.position.longitude, lat, lng)

                if(distance < LOCATION_NOISE_THRESHOLD) {
                    return@runOnUiThread
                }

                currentMarker.remove()
                zoom = mMap.cameraPosition.zoom
            }

            currentMarker = mMap.addMarker(MarkerOptions().position(location)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))!!

            polylineOptions.add(location).color(resources.getColor(R.color.dark_grey, null))
            mMap.addPolyline(polylineOptions)

            val cameraPosition: CameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(zoom).build()
            val cu = CameraUpdateFactory.newCameraPosition(cameraPosition)
            mMap.animateCamera(cu)

            longitudeTv.text = location.longitude.toString()
            latitudeTv.text = location.latitude.toString()
            lastUpdateTv.text = sdf.format(Date())
        }

    }
}