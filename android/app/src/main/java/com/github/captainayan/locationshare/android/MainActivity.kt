package com.github.captainayan.locationshare.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener{

    private val TAG: String = "MAIN ACTIVITY"

    private lateinit var toolbar: MaterialToolbar

    private lateinit var trackFriend: Button
    private lateinit var trackerBtn: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getString("uuid", "") == "") {
            sharedPreferences.edit().putString("uuid", UUID.randomUUID().toString()).apply()
        }

        toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        trackFriend = findViewById<Button>(R.id.track_a_friend_btn)
        trackerBtn = findViewById<Button>(R.id.tracker_btn)

        trackFriend.setOnClickListener(this)
        trackerBtn.setOnClickListener(this)

        if(!LocationService.IS_SERVICE_RUNNING) trackerBtn.text="Start Tracker"
        else trackerBtn.text="Stop Tracker"

    }

    override fun onClick(view: View?) {
        if(trackFriend.id == view?.id) {
            val intent: Intent = Intent(this, ChooseFriendActivity::class.java)
            startActivity(intent)
        }
        else if(trackerBtn.id == view?.id) {
            if(!LocationService.IS_SERVICE_RUNNING) {
                LocationService.startService(this, "Location Service is running...")
                (view as Button).text="Stop Tracker"
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
            startActivity(Intent(this@MainActivity, QRCodeActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}