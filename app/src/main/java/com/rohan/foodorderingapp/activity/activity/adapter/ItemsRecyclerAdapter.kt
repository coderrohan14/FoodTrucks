package com.rohan.foodorderingapp.activity.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.model.ItemsHistory

class ItemsRecyclerAdapter(val context: Context, val itemList: ArrayList<ItemsHistory>):

    RecyclerView.Adapter<ItemsRecyclerAdapter.ItemsViewHolder>() {
    class ItemsViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val txtHistoryItem:TextView = view.findViewById(R.id.txtHistoryItem)
        val txtHistoryPrice:TextView = view.findViewById(R.id.txtHistoryPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_items_single_row,parent,false)
        return ItemsRecyclerAdapter.ItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val home = itemList[position]
        holder.txtHistoryItem.text = home.name
        holder.txtHistoryPrice.text = "Rs."+home.price
    }
}
