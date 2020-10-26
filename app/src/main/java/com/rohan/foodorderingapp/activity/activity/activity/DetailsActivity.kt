package com.rohan.foodorderingapp.activity.activity.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.adapter.HomeRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.adapter.MenuRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.database.MenuDatabase
import com.rohan.foodorderingapp.activity.activity.database.MenuEntity
import com.rohan.foodorderingapp.activity.activity.model.Restaurant
import com.rohan.foodorderingapp.activity.activity.model.RestaurantDet
import com.rohan.foodorderingapp.activity.activity.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.recycler_menu_single_row.*
import org.json.JSONException
import java.lang.reflect.Method

class DetailsActivity : AppCompatActivity() {
    lateinit var recyclerDetails: RecyclerView
    lateinit var recyclerAdapter:MenuRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var detProgressLayout: RelativeLayout
    lateinit var detProgressBar: ProgressBar
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var toolbar: Toolbar
    lateinit var btnProceedToCart:Button
    lateinit var detFav:ImageView
    val menuItemList = arrayListOf<RestaurantDet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        recyclerDetails = findViewById(R.id.recyclerDetails)
        layoutManager = LinearLayoutManager(applicationContext)
        detFav = findViewById(R.id.imgDetFav)
        detProgressLayout = findViewById(R.id.detProgressLayout)
        detProgressBar = findViewById(R.id.detProgressBar)
        detProgressLayout.visibility = View.VISIBLE
        detProgressBar.visibility = View.VISIBLE
        restaurantId = intent.getStringExtra("restaurant_id")
        restaurantName = intent.getStringExtra("restaurant_name")
        var favCon = intent.getBooleanExtra("favourite_condition",false)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        btnProceedToCart.visibility = View.GONE
        toolbar = findViewById(R.id.detToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(favCon){
            detFav.setImageResource(R.mipmap.ic_heart_filled)
        }else{
            detFav.setImageResource(R.mipmap.ic_heart_empty)
        }
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
        if (ConnectionManager().checkConnectivity(this@DetailsActivity)) {
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    detProgressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        detProgressLayout.visibility = View.GONE
                        val resDetArray = data.getJSONArray("data")
                        for (i in 0 until resDetArray.length()) {
                            val restaurantJsonObject = resDetArray.getJSONObject(i)
                            val restaurantObject = RestaurantDet(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("restaurant_id")
                            )
                            menuItemList.add(restaurantObject)
                            recyclerAdapter =
                                MenuRecyclerAdapter(applicationContext,menuItemList)
                            recyclerDetails.adapter = recyclerAdapter
                            recyclerDetails.layoutManager = layoutManager
                            btnProceedToCart.visibility = View.VISIBLE
                        }
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
        queue.add(jsonObjectRequest)}else{
            val dialog = AlertDialog.Builder(this@DetailsActivity,R.style.MyDialogTheme)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DetailsActivity)
            }
            dialog.create()
            dialog.show()
        }
        btnProceedToCart.setOnClickListener {
            if(!DBAsyncTask(applicationContext,1).execute().get()){
                Toast.makeText(applicationContext,"No item added to the cart.",Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(applicationContext,CartActivity::class.java)
                intent.putExtra("restaurant_name",restaurantName)
                intent.putExtra("restaurant_id",restaurantId)
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        DBAsyncTask(applicationContext,2).execute()
        super.onBackPressed()
    }
    class DBAsyncTask(val context: Context,val mode:Int): AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, MenuDatabase::class.java, "menu-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
           when(mode){
               1->{
                   val cartList
                           :List<MenuEntity>
                   ?
                           = db.menuDao().getAllMenuItem()
                   if (cartList?.size == 0) {
                       return false
                   }
                   return true
               }
               2->{
                   db.menuDao().deleteAll()
                   return true
               }
           }
        return false
        }
    }
}
