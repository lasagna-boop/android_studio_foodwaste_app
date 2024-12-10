package com.example.foodwastemanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDao {

    // return ID
    @Insert
    suspend fun insertMeal(meal: Meal): Long

    @Delete
    suspend fun deleteMeal(meal: Meal)

    // select ALL meals from room db
    @Query("SELECT * FROM meal_table ORDER BY id DESC")
    suspend fun getAllMeals(): List<Meal>

    //for order by asc in the meal selector activity for prettier print
    @Query("SELECT * FROM meal_table ORDER BY meal_price ASC")
    suspend fun getMealsSortedByPrice(): List<Meal>
}