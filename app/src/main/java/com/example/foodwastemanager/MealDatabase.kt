package com.example.foodwastemanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Meal::class], version = 4) // not sure how else i could manage this without new dbs versions
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"
                )                                     //this code allowed me to clean the current dump and just move to later version without any damage
                    .fallbackToDestructiveMigration() // went through 4 different versions of database
                    .build()                          //i had to alter it while coding becasue came up with address idea
                INSTANCE = instance                   //thats why the current version is 4
                instance
            }
        }
    }
}