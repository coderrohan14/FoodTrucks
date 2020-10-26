package com.rohan.foodorderingapp.activity.activity.model

import org.json.JSONArray

data class RestaurantsHistory(
    val orderId:String,
    val restaurantName:String,
    val totalCost:String,
    val orderPlacedAt:String,
    val itemList:JSONArray)