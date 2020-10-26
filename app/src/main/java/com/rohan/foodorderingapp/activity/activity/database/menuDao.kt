package com.rohan.foodorderingapp.activity.activity.database

import android.os.FileObserver.DELETE
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Spliterator.DISTINCT

@Dao
interface menuDao {
    @Insert
    fun insertMenuItem(menuEntity: MenuEntity)
    @Delete
    fun deleteMenuItem(menuEntity: MenuEntity)
    @Query("SELECT * FROM cart")
    fun getAllMenuItem():List<MenuEntity>

    @Query("SELECT * FROM cart WHERE menuItemId= :menuItemId")
    fun getMenuItemById(menuItemId:String):MenuEntity
    @Query("DELETE FROM cart")
    fun deleteAll():Int
}
