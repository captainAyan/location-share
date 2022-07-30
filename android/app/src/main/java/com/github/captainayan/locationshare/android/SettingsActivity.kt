package com.github.captainayan.locationshare.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar


class SettingsActivity: AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById<View>(R.id.topAppBar) as MaterialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { this@SettingsActivity.finish() }

        if (findViewById<View?>(R.id.settingsFragmentContainer) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.settingsFragmentContainer, SettingsFragment()).commit()
        }
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            val c: Context? = context
            if (preference.title?.equals("View UUID") == true) {
                startActivity(Intent(c, QRCodeActivity::class.java))
            }
            else if (preference.title?.equals("Open source") == true) {
                val url: String = "https://github.com/captainAyan/location-share"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
            else if (preference.title?.equals("Privacy Policy") == true) {
                val url: String = "https://github.com/captainAyan/location-share/wiki/Privacy-Policy"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
            return super.onPreferenceTreeClick(preference)
        }
    }

}