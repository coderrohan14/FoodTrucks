package com.rohan.foodorderingapp.activity.activity.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.activity.DetailsActivity
import com.rohan.foodorderingapp.activity.activity.database.MenuDatabase
import com.rohan.foodorderingapp.activity.activity.database.MenuEntity
import com.rohan.foodorderingapp.activity.activity.database.RestaurantDatabase
import com.rohan.foodorderingapp.activity.activity.database.RestaurantEntity
import com.rohan.foodorderingapp.activity.activity.model.RestaurantDet
import com.squareup.picasso.Picasso

class MenuRecyclerAdapter(val context: Context,val menuList:List<RestaurantDet>): RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }
    override fun getItemCount(): Int {
        return menuList.size
    }
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuList[position]
        holder.txtItemName.text = item.menuItemName
        holder.txtItemPrice.text = "Rs." + item.menuItemCostForOne
        holder.txtItemNumber.text = (position+1).toString()
        val menuEntity = MenuEntity(item.menuItemId.toInt(),item.menuItemName,item.menuItemCostForOne)
        val checkFav = DBAsyncTask(context,menuEntity,1).execute()
        val isFav = checkFav.get()
        if(isFav){
            holder.btnAddToCart.text = "Remove"
            val favColor = ContextCompat.getColor(context,R.color.remove)
            holder.btnAddToCart.setBackgroundColor(favColor)
        }else{
            holder.btnAddToCart.text = "Add"
            val favColor = ContextCompat.getColor(context,R.color.colorPrimary)
            holder.btnAddToCart.setBackgroundColor(favColor)
        }
        holder.btnAddToCart.setOnClickListener {
            if(!DBAsyncTask(context,menuEntity,1).execute().get()){
                val async = DBAsyncTask(context,menuEntity,2).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Item added to cart", Toast.LENGTH_SHORT).show()
                    holder.btnAddToCart.text = "Remove"
                    val favColor = ContextCompat.getColor(context,R.color.remove)
                    holder.btnAddToCart.setBackgroundColor(favColor)
                }else{
                    Toast.makeText(context,"Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }else{
                val async = DBAsyncTask(context,menuEntity,3).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Item removed from cart", Toast.LENGTH_SHORT).show()
                    holder.btnAddToCart.text = "Add"
                    val favColor = ContextCompat.getColor(context,R.color.colorPrimary)
                    holder.btnAddToCart.setBackgroundColor(favColor)
                }else{
                    Toast.makeText(context,"Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val txtItemNumber:TextView = view.findViewById(R.id.txtItemNumber)
        val btnAddToCart:Button = view.findViewById(R.id.btnAddToCart)
    }
    class DBAsyncTask(val context: Context, val menuEntity: MenuEntity, val mode:Int) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, MenuDatabase::class.java, "menu-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val rest: MenuEntity? =
                        db.menuDao().getMenuItemById(menuEntity.menuItemId.toString())
                    db.close()
                    return rest != null
                }
                2 -> {
                    db.menuDao().insertMenuItem(menuEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.menuDao().deleteMenuItem(menuEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
    }



