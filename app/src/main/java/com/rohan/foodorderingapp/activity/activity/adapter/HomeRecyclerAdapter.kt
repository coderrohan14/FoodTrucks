package com.rohan.foodorderingapp.activity.activity.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.activity.DetailsActivity
import com.rohan.foodorderingapp.activity.activity.database.RestaurantDatabase
import com.rohan.foodorderingapp.activity.activity.database.RestaurantEntity
import com.rohan.foodorderingapp.activity.activity.model.Restaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_home_single_row.view.*

class HomeRecyclerAdapter(val context: Context,val itemList: ArrayList<Restaurant>):

    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){
    class HomeViewHolder(view: View):RecyclerView.ViewHolder(view){
        val restaurantName:TextView = view.findViewById(R.id.txtRestaurantName)
        val restaurantPrice:TextView = view.findViewById(R.id.txtRestaurantPrice)
        val restaurantRating:TextView = view.findViewById(R.id.txtRating)
        val restaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val linLay:LinearLayout = view.findViewById(R.id.linLay)
        val fav:ImageView = view.findViewById(R.id.imgHeartEmpty)
    }
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):HomeViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val home = itemList[position]
        var favCon = false
        holder.restaurantName.text=home.restaurantName
        holder.restaurantRating.text=home.restaurantRating
        holder.restaurantPrice.text="रु " + home.restaurantCostForOne + "/person"
        Picasso.get().load(home.restaurantImageUrl).error(R.drawable.food_logo).into(holder.restaurantImage)
        val restaurantEntity = RestaurantEntity(
            home.restaurantId.toInt(),
            home.restaurantName,
            home.restaurantRating,
            home.restaurantCostForOne,
            home.restaurantImageUrl
        )
        if(DBAsyncTask(context,restaurantEntity,1).execute().get()){
            holder.fav.setImageResource(R.mipmap.ic_heart_filled)
            favCon = true
        }
        else{
            holder.fav.setImageResource(R.mipmap.ic_heart_empty)
            favCon=false
        }
        holder.fav.setOnClickListener {
            if(!DBAsyncTask(context,restaurantEntity,1).execute().get()){
                val async = DBAsyncTask(context,restaurantEntity,2).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Restaurant added to favourites",Toast.LENGTH_SHORT).show()
                    holder.fav.setImageResource(R.mipmap.ic_heart_filled)
                    favCon = true
                }else{
                    Toast.makeText(context,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                }
            }else{
                val async = DBAsyncTask(context,restaurantEntity,3).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Restaurant removed from favourites",Toast.LENGTH_SHORT).show()
                    holder.fav.setImageResource(R.mipmap.ic_heart_empty)
                    favCon=false
                }else{
                    Toast.makeText(context,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                }
            }
            }
        holder.linLay.setOnClickListener {
            val intent = Intent(context.applicationContext,DetailsActivity::class.java)
            intent.putExtra("restaurant_id",home.restaurantId)
            intent.putExtra("restaurant_name",home.restaurantName)
            intent.putExtra("favourite_condition",favCon)
            context.startActivity(intent)
        }
        }
    }
    class DBAsyncTask(val context: Context,val restaurantEntity: RestaurantEntity,val mode:Int) : AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurant-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    val rest :RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return rest != null
                }
                2->{
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3->{
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

