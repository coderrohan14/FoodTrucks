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

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobile:EditText
    lateinit var etEmailEntered:EditText
    lateinit var btnNext: Button
    lateinit var forgotToolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        etMobile = findViewById(R.id.etMobile)
        etEmailEntered = findViewById(R.id.etEmailEntered)
        btnNext = findViewById(R.id.btnNext)
        sharedPreferences = getSharedPreferences(getString(R.string.forgot_preference),Context.MODE_PRIVATE)
        forgotToolbar = findViewById(R.id.forgotToolbar)
        setSupportActionBar(forgotToolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnNext.setOnClickListener {
            val mobileEntered = etMobile.text.toString()
            val emailEntered = etEmailEntered.text.toString()
            if (mobileEntered.length != 10) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Invalid mobile number entered",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileEntered)
                jsonParams.put("email", emailEntered)
                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val firstTry = data.getBoolean("first_try")
                                val reset = Intent(this@ForgotPasswordActivity,ResetActivity::class.java)
                                sharedPreferences.edit().putBoolean("first_try",firstTry).apply()
                                sharedPreferences.edit().putString("mobile_entered",mobileEntered).apply()
                                if(firstTry){
                                    Toast.makeText(applicationContext,"OTP sent to your Email.",Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(applicationContext,"Kindly use the previously sent OTP.",Toast.LENGTH_SHORT).show()
                                }
                                startActivity(reset)
                            } else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Incorrect Credentials!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        if (application != null) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
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
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity,R.style.MyDialogTheme)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
