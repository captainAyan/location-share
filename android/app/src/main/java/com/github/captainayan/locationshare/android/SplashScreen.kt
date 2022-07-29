package com.github.captainayan.locationshare.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*


class SplashScreen: AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getString("uuid", "") == "") {
            sharedPreferences.edit().putString("uuid", UUID.randomUUID().toString()).apply()
        }

        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}