package com.example.foodwastemanager

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MealSelectorActivity : AppCompatActivity() {

    private lateinit var mealDatabase: MealDatabase
    private lateinit var mealAdapter: MealAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector) // new screen without top part

        // Initialize the database and RecyclerView
        mealDatabase = MealDatabase.getDatabase(context = this)
        recyclerView = findViewById(R.id.mealRecyclerView)

        mealAdapter = MealAdapter { meal ->
            onMealSelected(meal) // Handle item clicks
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mealAdapter

        loadMeals()

        // Handle "Go Back" button click
        val goBackButton: Button = findViewById(R.id.goBackButton)
        goBackButton.setOnClickListener {
            finish() // Close the activity and go back to MainActivity
        }
    }

    private fun loadMeals() {
        CoroutineScope(Dispatchers.IO).launch {
            val meals = mealDatabase.mealDao().getAllMeals() // Fetch meals from the database
            withContext(Dispatchers.Main) {
                mealAdapter.submitList(meals) // Update RecyclerView with fetched meals
            }
        }
    }

    private fun onMealSelected(meal: Meal) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Meal")
        builder.setMessage ("Are you sure you want to get this meal? There is no backing up after this selection")

        builder.setPositiveButton("Yes") {_, _ ->

                CoroutineScope(Dispatchers.IO) .launch {
                    mealDatabase.mealDao().deleteMeal(meal)
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            this@MealSelectorActivity,
                            "Meal saved successfully!",
                            Toast.LENGTH_SHORT
                            ).show()
                            loadMeals()
                    }
            }
        }
        builder.setNegativeButton("No!"){dialog,_->
                dialog.dismiss()
        }
        val dialog = builder.create ()
        dialog.show()
    }
}