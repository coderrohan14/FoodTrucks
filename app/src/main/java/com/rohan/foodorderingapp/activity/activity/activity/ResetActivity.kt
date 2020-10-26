package com.rohan.foodorderingapp.activity.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetActivity : AppCompatActivity() {
    lateinit var resetToolbar: Toolbar
    lateinit var txtTitle:TextView
    lateinit var etOtp:EditText
    lateinit var etNewPassword:EditText
    lateinit var etConfirmNewPassword:EditText
    lateinit var btnReset:Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        resetToolbar = findViewById(R.id.resetToolbar)
        setSupportActionBar(resetToolbar)
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        txtTitle = findViewById(R.id.txtTitle)
        etOtp = findViewById(R.id.etOtp)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        sharedPreferences = getSharedPreferences(getString(R.string.forgot_preference), Context.MODE_PRIVATE)
        btnReset = findViewById(R.id.btnReset)
        val firstTry:Boolean = sharedPreferences.getBoolean("first_try",true)
        if(firstTry){
            txtTitle.text = "Enter the OTP you received below :"
        }else{
            txtTitle.text = "Enter the previously sent OTP :"
        }
        btnReset.setOnClickListener {
            val mobileNumber = sharedPreferences.getString("mobile_entered",null)
            val otp = etOtp.text.toString()
            val pass = etNewPassword.text.toString()
            val confirmPass = etConfirmNewPassword.text.toString()
            if(pass==confirmPass){
                if(pass.length>=6){
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", pass)
                    jsonParams.put("otp",otp)
                    val queue = Volley.newRequestQueue(this@ResetActivity)
                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                    if (ConnectionManager().checkConnectivity(this@ResetActivity)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val new1 = Intent(this@ResetActivity,LoginActivity::class.java)
                                    val successMessage = data.getString("successMessage")
                                    Toast.makeText(applicationContext,successMessage,Toast.LENGTH_SHORT).show()
                                    sharedPreferences.edit().clear().apply()
                                    startActivity(new1)
                                } else {
                                    Toast.makeText(
                                        this@ResetActivity,
                                        "Incorrect Credentials!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ResetActivity,
                                    "Some unexpected error occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            if (application != null) {
                                Toast.makeText(
                                    this@ResetActivity,
                                    "Volley error occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "d99f7c1efd5d4a"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)}else{
                        val dialog = AlertDialog.Builder(this@ResetActivity,R.style.MyDialogTheme)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection Not Found")
                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@ResetActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }else{
                    Toast.makeText(this@ResetActivity,"The password should have minimum 6 characters!! ",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this@ResetActivity,"The passwords don't match.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
