package com.example.foodwastemanager

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "meal_table")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "meal_name")
    val mealName: String,

    @ColumnInfo(name = "meal_price")
    val price: Double,

    @ColumnInfo(name = "restaurant_name")
    val restaurantName: String
)