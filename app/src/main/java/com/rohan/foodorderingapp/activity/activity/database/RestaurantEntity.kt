package com.rohan.foodorderingapp.activity.activity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class RestaurantEntity(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "name") val restaurantName:String,
    @ColumnInfo(name = "rating") val restaurantRating:String,
    @ColumnInfo(name = "cost_for_one") val restaurantCostForOne:String,
    @ColumnInfo(name = "image_url") val restaurantImageUrl:String)