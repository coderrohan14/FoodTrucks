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
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var etLoginId : EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    lateinit var txtRegister : TextView
    lateinit var txtForgot : TextView
    lateinit var sharedPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreference = getSharedPreferences(getString(R.string.data_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreference.getBoolean("isLoggedIn",false)
        if(isLoggedIn) {
            val loggedIn = Intent(this@LoginActivity, LoggedInActivity::class.java)
            startActivity(loggedIn)
            finish()
        }
        title="Login Page"
        etLoginId = findViewById(R.id.etLoginId)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)
        txtForgot = findViewById(R.id.txtForgot)
        txtRegister.setOnClickListener {
            val next = Intent(this@LoginActivity,
                RegisterPage::class.java)
            startActivity(next)
        }
        txtForgot.setOnClickListener {
            val new3 = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(new3)
        }
        btnLogin.setOnClickListener {
            val mobileNumberEntered = etLoginId.text.toString()
            val loginPasswordEntered  =etPassword.text.toString()
            if(mobileNumberEntered.length!=10||loginPasswordEntered.length<4){
                Toast.makeText(this@LoginActivity,"Invalid Phone number or password",Toast.LENGTH_SHORT).show()
            }else {
                val intent = Intent(
                    this@LoginActivity,
                    LoggedInActivity::class.java
                )
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumberEntered)
                jsonParams.put("password", loginPasswordEntered)
                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val loginJsonObject = data.getJSONObject("data")
                                val userId = loginJsonObject.getString("user_id").toString()
                                val nameEntered = loginJsonObject.getString("name").toString()
                                val emailEntered = loginJsonObject.getString("email").toString()
                                val mobileEntered =
                                    loginJsonObject.getString("mobile_number").toString()
                                val addressEntered = loginJsonObject.getString("address").toString()
                                sharedPreference.edit().putString("UserId", userId).apply()
                                sharedPreference.edit().putString("Name", nameEntered).apply()
                                sharedPreference.edit().putString("Email", emailEntered).apply()
                                sharedPreference.edit().putString("Mobile", mobileEntered).apply()
                                sharedPreference.edit().putString("Address", addressEntered).apply()
                                sharedPreference.edit().putBoolean("isLoggedIn",true).apply()
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Incorrect Credentials!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        if (application != null) {
                            Toast.makeText(
                                this@LoginActivity,
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
                    val dialog = AlertDialog.Builder(this@LoginActivity,R.style.MyDialogTheme)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
