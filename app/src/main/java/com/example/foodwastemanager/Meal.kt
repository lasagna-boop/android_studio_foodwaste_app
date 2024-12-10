package com.example.foodwastemanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//class meal to store everything about a single entity
@Entity(tableName = "meal_table")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "meal_name")
    val mealName: String,

    @ColumnInfo(name = "meal_price")
    val price: Double,

    @ColumnInfo(name = "restaurant_name")
    val restaurantName: String,

    @ColumnInfo(name = "address")
    val address: String
)