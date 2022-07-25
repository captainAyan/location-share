package com.github.captainayan.locationshare.android;

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

class QRCodeActivity : AppCompatActivity(){

    private lateinit var qrCodeIV: ImageView
    private lateinit var generateNewCodeBtn: Button
    private lateinit var uuidTV: TextView

    private lateinit var uuid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        uuid = sharedPreferences.getString("uuid", "").toString()

        qrCodeIV = findViewById<ImageView>(R.id.qr_code_view);
        generateNewCodeBtn = findViewById<Button>(R.id.generate_new_code_btn)
        uuidTV = findViewById<TextView>(R.id.uuid_tv)

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