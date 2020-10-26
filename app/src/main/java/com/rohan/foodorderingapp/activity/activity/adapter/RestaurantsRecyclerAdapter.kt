package com.rohan.foodorderingapp.activity.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.model.ItemsHistory
import com.rohan.foodorderingapp.activity.activity.model.RestaurantsHistory

class RestaurantsRecyclerAdapter(val context: Context, val itemList: ArrayList<RestaurantsHistory>):

    RecyclerView.Adapter<RestaurantsRecyclerAdapter.RestaurantsViewHolder>(){

    class RestaurantsViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtRestaurantHistory:TextView = view.findViewById(R.id.txtRestaurantHistory)
        val txtRestaurantTotal:TextView = view.findViewById(R.id.txtRestaurantTotal)
        val txtDateHistory:TextView = view.findViewById(R.id.txtDateHistory)
        val recyclerItems:RecyclerView = view.findViewById(R.id.RecyclerItems)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantsRecyclerAdapter.RestaurantsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurants_single_row,parent,false)
        return RestaurantsRecyclerAdapter.RestaurantsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: RestaurantsRecyclerAdapter.RestaurantsViewHolder,
        position: Int
    ) {
        val restaurant = itemList[position]
        holder.txtRestaurantHistory.text =restaurant.restaurantName
        holder.txtRestaurantTotal.text = "Total Cost : Rs."+restaurant.totalCost
        holder.txtDateHistory.text = restaurant.orderPlacedAt
        val foodItemsList = ArrayList<ItemsHistory>()
        for (i in 0 until restaurant.itemList.length()) {
            val foodJson = restaurant.itemList.getJSONObject(i)
            foodItemsList.add(
                ItemsHistory(
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val itemsLayout:RecyclerView.LayoutManager = LinearLayoutManager(context)
            val itemsAdapter: ItemsRecyclerAdapter = ItemsRecyclerAdapter(context,foodItemsList)
            holder.recyclerItems.layoutManager = itemsLayout
            holder.recyclerItems.adapter = itemsAdapter
    }

}