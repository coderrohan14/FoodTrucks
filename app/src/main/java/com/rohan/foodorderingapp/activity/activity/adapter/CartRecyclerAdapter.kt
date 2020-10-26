package com.rohan.foodorderingapp.activity.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohan.foodorderingapp.R
import com.rohan.foodorderingapp.activity.activity.database.MenuEntity
import org.w3c.dom.Text

class CartRecyclerAdapter(val context: Context, val menuList:List<MenuEntity>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = menuList[position]
        holder.txtCartListItemName.text = item.menuItemName
        holder.txtCartListItemPrice.text = "Rs." + item.menuItemCostForOne
    }
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtCartListItemName:TextView = view.findViewById(R.id.txtCartListItem)
        val txtCartListItemPrice:TextView = view.findViewById(R.id.txtCartListItemPrice)
    }

}