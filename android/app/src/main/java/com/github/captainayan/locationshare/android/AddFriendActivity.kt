package com.github.captainayan.locationshare.android;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar

import com.google.android.material.textfield.TextInputEditText;
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

class AddFriendActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var nameInput: TextInputEditText;
    private lateinit var uuidInput: TextInputEditText;
    private lateinit var qrCodeBtn: ImageButton;
    private lateinit var submitBtn: Button;

    private lateinit var friendDao: DB.FriendDao

    private val scanQrCode = registerForActivityResult(ScanQRCode(), ::handleResult)

    private val pattern = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}\$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        nameInput = findViewById<TextInputEditText>(R.id.nameTxtInp)
        uuidInput = findViewById<TextInputEditText>(R.id.uuidTxtInp)
        qrCodeBtn = findViewById<ImageButton>(R.id.qrCodeBtn)
        submitBtn = findViewById<Button>(R.id.submitBtn)

        friendDao = DB.FriendDatabase.getDatabase(this).friendDao()

        qrCodeBtn.setOnClickListener(this)
        submitBtn.setOnClickListener(this)

        toolbar = findViewById<View>(R.id.topAppBar) as MaterialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { this@AddFriendActivity.finish() }
    }

    private fun handleResult(result: QRResult) {
        if (result is QRResult.QRSuccess) {
            uuidInput.setText(result.content.rawValue);
        }
        else Toast.makeText(this, "Scanning failed", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        if(qrCodeBtn.id == view?.id) {
            scanQrCode.launch(null)
        }
        else if(submitBtn.id == view?.id) {
            val name:String = nameInput.text.toString().trim()
            val uuid:String = uuidInput.text.toString().trim()

            if (name.length > 20)
                Toast.makeText(this, "Name cannot exceed 20 characters", Toast.LENGTH_LONG).show()
            else if (name.isEmpty())
                Toast.makeText(this, "Name field cannot be empty", Toast.LENGTH_LONG).show()
            else if (!pattern.matches(uuid))
                Toast.makeText(this, "UUID is not valid", Toast.LENGTH_LONG).show()
            else {
                friendDao.insert(DB.Friend(name, uuid))
                Toast.makeText(this, "Friend saved", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}