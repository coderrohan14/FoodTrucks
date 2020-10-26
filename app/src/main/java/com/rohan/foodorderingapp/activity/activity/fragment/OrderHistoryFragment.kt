package com.rohan.foodorderingapp.activity.activity.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.adapter.ItemsRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.adapter.RestaurantsRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.model.ItemsHistory
import com.rohan.foodorderingapp.activity.activity.model.RestaurantsHistory
import com.rohan.foodorderingapp.activity.activity.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import org.json.JSONException

class OrderHistoryFragment:Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayoutHistory:RelativeLayout
    lateinit var progressBarHistory:ProgressBar
    lateinit var recyclerRestaurants:RecyclerView
    lateinit var restaurantsLayout:RecyclerView.LayoutManager
    lateinit var itemsLayout:RecyclerView.LayoutManager
    lateinit var restaurantsAdapter:RestaurantsRecyclerAdapter
    val resHistory= arrayListOf<RestaurantsHistory>()
    lateinit var txtEmpty:TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        setHasOptionsMenu(true)
        progressLayoutHistory = view.findViewById(R.id.progressLayoutHistory)
        progressBarHistory = view.findViewById(R.id.progressBarHistory)
        progressLayoutHistory.visibility = View.VISIBLE
        progressBarHistory.visibility = View.VISIBLE
        txtEmpty = view.findViewById(R.id.txtEmpty)
        txtEmpty.visibility = View.GONE
        recyclerRestaurants = view.findViewById(R.id.recyclerRestaurants)
        restaurantsLayout = LinearLayoutManager(activity as Context)
        itemsLayout = LinearLayoutManager(activity as Context)
        sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.data_file_name), Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("UserId",null)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url,null, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progressLayoutHistory.visibility = View.GONE
                        val historyJsonObject = data.getJSONArray("data")
                        if(historyJsonObject.length()==0){
                            txtEmpty.visibility = View.VISIBLE
                        }else{
                        for(i in 0 until historyJsonObject.length()){
                            val temp = historyJsonObject.getJSONObject(i)
                            val resObject = RestaurantsHistory(
                                temp.getString("order_id"),
                                temp.getString("restaurant_name"),
                                temp.getString("total_cost"),
                                temp.getString("order_placed_at"),
                                temp.getJSONArray("food_items")
                            )
                            resHistory.add(resObject)
                            restaurantsAdapter = RestaurantsRecyclerAdapter(activity as Context,resHistory)
                            recyclerRestaurants.adapter = restaurantsAdapter
                            recyclerRestaurants.layoutManager = restaurantsLayout
                            }}
                        } else {
                        Toast.makeText(
                            activity,
                            "Some error occurred..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        activity,
                        "Some unexpected error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                if (activity != null) {
                    Toast.makeText(
                        activity,
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
            val dialog = AlertDialog.Builder(activity as Context,R.style.MyDialogTheme)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
}
