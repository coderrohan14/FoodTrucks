package com.rohan.foodorderingapp.activity.activity.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.adapter.HomeRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.model.Restaurant
import com.rohan.foodorderingapp.activity.activity.util.ConnectionManager
import org.json.JSONException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.HashMap

class HomeFragment:Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val homeInfoList = arrayListOf<Restaurant>()
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    var costComparator = kotlin.Comparator<Restaurant> { restaurant1,restaurant2 ->
        if(restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne,true)==0){
            restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne,true)
        }else {
            restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne, true)
        }
    }
    var ratingComparator = kotlin.Comparator<Restaurant> { restaurant1,restaurant2 ->
        if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)==0){
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
        }else {
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val restaurantJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                homeInfoList.add(restaurantObject)
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, homeInfoList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error has occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String,String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d99f7c1efd5d4a"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)

        } else {
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        when(id){
            R.id.action_sort_cost_low ->{
                Collections.sort(homeInfoList,costComparator)
                homeInfoList.reverse()
                item.setChecked(true)
                item.setIcon(R.mipmap.ic_down)
            }
            R.id.action_sort_cost_high -> {
                Collections.sort(homeInfoList,costComparator)
                item.setChecked(true)
                item.setIcon(R.mipmap.ic_up)
            }
            R.id.action_sort_rating_low -> {
                Collections.sort(homeInfoList,ratingComparator)
                homeInfoList.reverse()
                item.setChecked(true)
                item.setIcon(R.mipmap.ic_down)
            }
            R.id.action_sort_rating_high -> {
                Collections.sort(homeInfoList,ratingComparator)
                item.setChecked(true)
                item.setIcon(R.mipmap.ic_up)
            }
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}