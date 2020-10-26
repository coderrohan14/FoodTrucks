package com.rohan.foodorderingapp.activity.activity.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.adapter.MenuRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.model.RestaurantDet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterPage : AppCompatActivity() {
    lateinit var sharedPreference:SharedPreferences
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etDeliveryApp:EditText
    lateinit var etMobileNo:EditText
    lateinit var etPasswordRegister:EditText
    lateinit var registerToolbar: Toolbar
    lateinit var etConfirmPassword:EditText
    lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)
        sharedPreference = getSharedPreferences(getString(R.string.data_file_name), Context.MODE_PRIVATE)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNo = findViewById(R.id.etMobileNo)
        etDeliveryApp = findViewById(R.id.etDeliveryAddress)
        etPasswordRegister = findViewById(R.id.etPasswordRegister)
        registerToolbar = findViewById(R.id.registerToolbar)
        setSupportActionBar(registerToolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            if(etMobileNo.text.toString().length==10){
            val nameEntered = etName.text.toString()
            val emailEntered =etEmail.text.toString()
            val mobileEntered = etMobileNo.text.toString()
            val addressEntered = etDeliveryApp.text.toString()
            val passwordEntered = etPasswordRegister.text.toString()
            val confirmPasswordEntered = etConfirmPassword.text.toString()
                if(confirmPasswordEntered==passwordEntered){
            val jsonParams = JSONObject()
                jsonParams.put("name", nameEntered)
                jsonParams.put("mobile_number", mobileEntered)
                jsonParams.put("password", passwordEntered)
                jsonParams.put("address", addressEntered)
                jsonParams.put("email", emailEntered)
            val next1 = Intent(this@RegisterPage,
                LoggedInActivity::class.java)
                val queue = Volley.newRequestQueue(this@RegisterPage)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url,jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val registerJsonObject = data.getJSONObject("data")
                                val userId = registerJsonObject.getString("user_id").toString()
                                val name = registerJsonObject.getString("name").toString()
                                val email = registerJsonObject.getString("email").toString()
                                val mobileNumber = registerJsonObject.getString("mobile_number").toString()
                                val address = registerJsonObject.getString("address").toString()
                                sharedPreference.edit().putString("UserId",userId).apply()
                                sharedPreference.edit().putString("Name",name).apply()
                                sharedPreference.edit().putString("Email",email).apply()
                                sharedPreference.edit().putString("Mobile",mobileNumber).apply()
                                sharedPreference.edit().putString("Address",address).apply()
                                startActivity(next1)
                                } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Some error has occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                applicationContext,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        if (application != null) {
                            Toast.makeText(
                                applicationContext,
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
                queue.add(jsonObjectRequest)} else{
                Toast.makeText(this@RegisterPage,"The passwords entered don't match",Toast.LENGTH_LONG).show()
            }}else{
                Toast.makeText(this@RegisterPage,"Invalid Phone Number!!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
