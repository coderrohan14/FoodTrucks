package com.rohan.foodorderingapp.activity.activity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class MenuEntity(
    @PrimaryKey val menuItemId:Int,
    @ColumnInfo(name = "name") val menuItemName:String,
    @ColumnInfo(name = "cost_for_one") val menuItemCostForOne:String)