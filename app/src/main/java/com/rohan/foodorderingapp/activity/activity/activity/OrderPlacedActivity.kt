package com.rohan.foodorderingapp.activity.activity.activity

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.rohan.foodorderingapp.R

class OrderPlacedActivity : AppCompatActivity() {
    lateinit var btnOk : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        btnOk = findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val ok = Intent(this@OrderPlacedActivity,LoggedInActivity::class.java)
            startActivity(ok)
        }
    }
}
