package com.example.foodwastemanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var mealDatabase: MealDatabase
    private lateinit var mealAdapter: MealAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Initialize the database
        mealDatabase = MealDatabase.getDatabase(context = this)

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.mealRecyclerView)
        mealAdapter = MealAdapter {}
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mealAdapter

        // Load meals into RecyclerView
        loadMeals()

        // Handle Save Button Click
        val mealNameInput: EditText = findViewById(R.id.mealNameInput)
        val priceInput: EditText = findViewById(R.id.priceInput)
        val restaurantNameInput: EditText = findViewById(R.id.restaurantNameInput)
        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val mealName = mealNameInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val restaurantName = restaurantNameInput.text.toString()

            if (mealName.isNotEmpty() && restaurantName.isNotEmpty()) {
                val meal = Meal(mealName = mealName, price = price, restaurantName = restaurantName)
                saveMeal(meal)

                // Clear input fields after saving
                mealNameInput.text.clear()
                priceInput.text.clear()
                restaurantNameInput.text.clear()
            } else {
                println("Please fill in all fields") // Optional: Replace with Toast for better UX
            }
        }

        // Navigate to MealSelectorActivity
        val goToSelectorButton: Button = findViewById(R.id.goToSelectorButton)
        goToSelectorButton.setOnClickListener {
            val intent = Intent(this, MealSelectorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload meals to reflect changes made in MealSelectorActivity
        loadMeals()
    }

    // Load meals from database into RecyclerView
    private fun loadMeals() {
        CoroutineScope(Dispatchers.IO).launch {
            val meals = mealDatabase.mealDao().getAllMeals() // Fetch meals from the database
            withContext(Dispatchers.Main) {
                if (meals.isEmpty()) {
                    findViewById<TextView>(R.id.noMealsTextView).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.mealRecyclerView).visibility = View.GONE
                } else {
                    findViewById<TextView>(R.id.noMealsTextView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.mealRecyclerView).visibility = View.VISIBLE
                }
                mealAdapter.submitList(meals) // Update RecyclerView adapter
            }
        }
    }

    // Save meal to the database
    private fun saveMeal(meal: Meal) {
        CoroutineScope(Dispatchers.IO).launch {
            val mealId = mealDatabase.mealDao().insertMeal(meal)
            if (mealId > 0) {
                println("Meal successfully inserted with ID: $mealId")
                loadMeals() // Reload RecyclerView after insertion
            } else {
                println("Failed to insert meal")
            }
        }
    }
}