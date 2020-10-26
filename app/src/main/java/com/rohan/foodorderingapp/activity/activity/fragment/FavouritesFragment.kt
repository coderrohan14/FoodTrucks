package com.rohan.foodorderingapp.activity.activity.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.adapter.FavouriteRecyclerAdapter
import com.rohan.foodorderingapp.activity.activity.database.RestaurantDatabase
import com.rohan.foodorderingapp.activity.activity.database.RestaurantEntity

class FavouritesFragment:Fragment() {
    lateinit var recyclerFavourite:RecyclerView
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FavouriteRecyclerAdapter
    var dbRestaurantList = listOf<RestaurantEntity>()
    lateinit var txtAddItems:TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        setHasOptionsMenu(true)
        recyclerFavourite = view.findViewById(R.id.recyclerFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(activity as Context)
        txtAddItems = view.findViewById(R.id.txtAddItems)
        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()
        if(dbRestaurantList.isEmpty()){
            progressLayout.visibility = View.GONE
            txtAddItems.visibility = View.VISIBLE
        }else {
            txtAddItems.visibility = View.GONE
            if (activity != null) {
                progressLayout.visibility = View.GONE
                recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
                recyclerFavourite.adapter = this.recyclerAdapter
                recyclerFavourite.layoutManager = layoutManager
            }
        }
        return view
    }
    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-db").build()
            return db.restaurantDao().getAllRestaurants()
        }

    }
}
