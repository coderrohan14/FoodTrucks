package com.rohan.foodorderingapp.activity.activity.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface restaurantDao {
    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)
    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)
    @Query("SELECT * FROM data")
    fun getAllRestaurants():List<RestaurantEntity>

    @Query("SELECT * FROM data WHERE id= :restaurantId")
    fun getRestaurantById(restaurantId:String):RestaurantEntity
    @Query("DELETE FROM data")
    fun deleteAll():Int
}
