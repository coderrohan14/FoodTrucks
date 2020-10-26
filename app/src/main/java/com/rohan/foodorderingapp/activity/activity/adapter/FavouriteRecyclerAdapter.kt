package com.rohan.foodorderingapp.activity.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context,val itemList:List<RestaurantEntity>):RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourites_single_row, parent, false)
        return FavouriteViewHolder(view)
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val item = itemList[position]
        holder.txtFavRestaurantName.text = item.restaurantName
        holder.txtFavRestaurantPrice.text = "रु " + item.restaurantCostForOne + "/person"
        holder.txtFavRestaurantRating.text = item.restaurantRating
        Picasso.get().load(item.restaurantImageUrl).error(R.mipmap.ic_food)
            .into(holder.imgFavRestaurantImage)
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFavRestaurantName: TextView = view.findViewById(R.id.txtFavRestaurantName)
        val txtFavRestaurantPrice: TextView = view.findViewById(R.id.txtFavRestaurantPrice)
        val txtFavRestaurantRating: TextView = view.findViewById(R.id.txtFavRating)
        val imgFavRestaurantImage: ImageView = view.findViewById(R.id.imgFavRestaurantImage)
        val llContent: LinearLayout = view.findViewById(R.id.favLinLay)
    }


}