package com.github.captainayan.locationshare.android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog


class MainActivity : AppCompatActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks{

    private val TAG: String = "MAIN ACTIVITY"

    private lateinit var toolbar: MaterialToolbar

    private lateinit var trackFriend: Button
    private lateinit var trackerBtn: Button

    private val PERMISSION_LOCATION_REQUEST_CODE: Int = 1

    private var lm: LocationManager? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!hasLocationPermissions()) {
            requestLocationPermission()
        }

        toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        trackFriend = findViewById<Button>(R.id.trackAFriendBtn)
        trackerBtn = findViewById<Button>(R.id.trackerBtn)

        trackFriend.setOnClickListener(this)
        trackerBtn.setOnClickListener(this)

        if(!LocationService.IS_SERVICE_RUNNING) trackerBtn.text="Start Tracker"
        else trackerBtn.text="Stop Tracker"
        
        lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    override fun onClick(view: View?) {
        if(trackFriend.id == view?.id) {
            val intent: Intent = Intent(this, ChooseFriendActivity::class.java)
            startActivity(intent)
        }
        else if(trackerBtn.id == view?.id) {
            if(!LocationService.IS_SERVICE_RUNNING) {
                if(EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                        LocationService.startService(this, "Location Service is running...")
                        (view as Button).text="Stop Tracker"
                    }
                    else {
                        Toast.makeText(this, "Turn on GPS.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
                    EasyPermissions.requestPermissions(
                        this,
                        "This application cannot work without location permission.",
                        PERMISSION_LOCATION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
            else {
                LocationService.stopService(this)
                (view as Button).text="Start Tracker"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        if (item.itemId == R.id.main_menu_settings) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hasLocationPermissions() =
        EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without location permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionDenied(this, perms.first())) {
            SettingsDialog.Builder(this@MainActivity).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, "Location Share is ready", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}