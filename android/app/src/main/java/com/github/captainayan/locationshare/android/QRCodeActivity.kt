package com.github.captainayan.locationshare.android;

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*


class QRCodeActivity : AppCompatActivity(){

    private lateinit var toolbar: MaterialToolbar

    private lateinit var qrCodeIV: ImageView
    private lateinit var generateNewCodeBtn: Button
    private lateinit var uuidTV: TextView

    private lateinit var uuid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        uuid = sharedPreferences.getString("uuid", "").toString()

        qrCodeIV = findViewById<ImageView>(R.id.qrCodeView);
        generateNewCodeBtn = findViewById<Button>(R.id.generateNewCodeBtn)
        uuidTV = findViewById<TextView>(R.id.uuidTv)

        this.showQRCode(uuid)

        generateNewCodeBtn.setOnClickListener() {_ ->
            sharedPreferences.edit().putString("uuid", UUID.randomUUID().toString()).apply()
            uuid = sharedPreferences.getString("uuid", "").toString()
            showQRCode(uuid)
        }
        
        uuidTV.setOnLongClickListener {
            copyUuid(uuidTV.text.toString())
            Toast.makeText(this, "UUID copied", Toast.LENGTH_SHORT).show()
            true
        }

        toolbar = findViewById<View>(R.id.topAppBar) as MaterialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { this@QRCodeActivity.finish() }
    }

    private fun showQRCode(uuid:String) {
        uuidTV.text = uuid

        val writer: MultiFormatWriter = MultiFormatWriter()
        val matrix:BitMatrix = writer.encode(uuid, BarcodeFormat.QR_CODE, 300, 300)

        val encoder: BarcodeEncoder = BarcodeEncoder()
        val bitmap:Bitmap = encoder.createBitmap(matrix)

        qrCodeIV.setImageBitmap(bitmap)
    }

    private fun copyUuid(text: CharSequence) {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("copy text", text)
        clipboard.setPrimaryClip(clip)
    }
}