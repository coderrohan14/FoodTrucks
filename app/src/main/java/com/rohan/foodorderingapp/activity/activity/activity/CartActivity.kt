package com.rohan.foodorderingapp.activity.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.adapter.CartRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.database.MenuDatabase
import com.rohan.foodorderingapp.activity.activity.database.MenuEntity
import com.rohan.foodorderingapp.activity.activity.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var recyclerCart:RecyclerView
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartProgressLayout:RelativeLayout
    lateinit var cartProgressBar:ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var btnPlaceOrder:Button
    lateinit var txtOrderingFrom:TextView
    lateinit var cartItem :List<MenuEntity>
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.cartToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerCart = findViewById(R.id.recyclerCart)
        layoutManager = LinearLayoutManager(applicationContext)
        cartProgressLayout = findViewById(R.id.cartProgressLayout)
        cartProgressBar = findViewById(R.id.cartProgressBar)
        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)
        sharedPreferences = getSharedPreferences(getString(R.string.data_file_name), Context.MODE_PRIVATE)
        var restaurantName = intent.getStringExtra("restaurant_name").toString()
        var restaurantId = intent.getStringExtra("restaurant_id").toString()
        val userId = sharedPreferences.getString("UserId",null).toString()
        cartProgressLayout.visibility = View.VISIBLE
        cartProgressBar.visibility = View.VISIBLE
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtOrderingFrom.text = restaurantName
        cartItem  = DBAsyncTask(applicationContext,1).execute().get()
            recyclerAdapter = CartRecyclerAdapter(applicationContext, cartItem)
            cartProgressLayout.visibility = View.GONE
            recyclerCart.adapter = recyclerAdapter
            recyclerCart.layoutManager = layoutManager
        var price=0
        for(element in cartItem){
            price+= element.menuItemCostForOne.toInt()
        }
        btnPlaceOrder.text = "Place Order(TOTAL:   Rs. $price)"
        btnPlaceOrder.setOnClickListener {
            val intent = Intent(
                this@CartActivity,
                OrderPlacedActivity::class.java)
            val finalPrice = price.toString()
            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", restaurantId)
            jsonParams.put("total_cost",finalPrice)
            val foodArray = JSONArray()
            for (i in cartItem.indices) {
                val foodId = JSONObject()
                foodId.put("food_item_id", cartItem[i].menuItemId.toString())
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)
            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val mp: MediaPlayer = MediaPlayer.create(applicationContext,R.raw.bell)
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            DBAsyncTask(this@CartActivity,2)
                            mp.start()
                           startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@CartActivity,
                                "Some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@CartActivity,
                            "Some unexpected error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (application != null) {
                        Toast.makeText(
                            this@CartActivity,
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
                val dialog = AlertDialog.Builder(this@CartActivity,R.style.MyDialogTheme)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
  }
    class DBAsyncTask(val context: Context,val mode:Int): AsyncTask<Void,Void,List<MenuEntity>>(){
        lateinit var cartList:List<MenuEntity>
        val db = Room.databaseBuilder(context, MenuDatabase::class.java, "menu-db").build()
        override fun doInBackground(vararg params: Void?): List<MenuEntity>{
            when(mode){
                1->{
                    cartList = db.menuDao().getAllMenuItem()
                }
                2->{
                    db.menuDao().deleteAll()
                }
            }
            return cartList
        }
    }
}

