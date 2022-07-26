package com.github.captainayan.locationshare.android

import Utility
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.captainayan.locationshare.android.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_NOISE_THRESHOLD: Double = 0.0004 // IMPORTANT tune the threshold

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var polylineOptions: PolylineOptions = PolylineOptions()
    private lateinit var currentMarker:Marker;
    private var zoom:Float = 15.0f

    private lateinit var uuid: String
    private var url: String = BuildConfig.SERVER_BASE_URL+"/?uuid="
    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        uuid = intent.getStringExtra("uuid").toString()
        url += uuid

        Toast.makeText(this, url, Toast.LENGTH_SHORT).show()


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

            if (polylineOptions.points.size == 0) {
                currentMarker = mMap.addMarker(MarkerOptions().position(location)
                    .title("Starting Location")
                    .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))!!

                polylineOptions.add(location).color(resources.getColor(R.color.white, null))
            }
            else {
                zoom = mMap.cameraPosition.zoom

                val distance: Double = Utility.getLocationDifference(currentMarker.position.latitude,
                    currentMarker.position.longitude,
                    lat,
                    lng)


                if(distance > LOCATION_NOISE_THRESHOLD) {

                    currentMarker.remove()

                    currentMarker =
                        mMap.addMarker(MarkerOptions().position(location).title("Current Location")
                            .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))!!

                    polylineOptions.add(location).color(resources.getColor(R.color.white, null))
                }

            }

//            polylineOptions.add(location).color(resources.getColor(R.color.white, null))
            mMap.addPolyline(polylineOptions)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }

    }
}