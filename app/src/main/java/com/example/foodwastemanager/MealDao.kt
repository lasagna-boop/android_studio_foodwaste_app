package com.example.foodwastemanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDao {

    // Insert a meal and return its generated ID
    @Insert
    suspend fun insertMeal(meal: Meal): Long

    @Delete
    suspend fun deleteMeal(meal: Meal)

    // Fetch all meals from the database
    @Query("SELECT * FROM meal_table ORDER BY id DESC")
    suspend fun getAllMeals(): List<Meal>
}