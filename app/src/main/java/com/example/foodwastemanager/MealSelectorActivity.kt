package com.example.foodwastemanager

import android.content.Intent
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

        // reinit db and rec.view
        mealDatabase = MealDatabase.getDatabase(context = this)
        recyclerView = findViewById(R.id.mealRecyclerView)

        mealAdapter = MealAdapter { meal ->
            onMealSelected(meal) // again handling clicks
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mealAdapter

        loadMeals()

        // goback logic once again
        val goBackButton: Button = findViewById(R.id.goBackButton)
        goBackButton.setOnClickListener {
            finish() // go to MAinActivity.kt
        }

        // sorting (asc in db call)
        val sortButton: Button = findViewById(R.id.sortButton)
        sortButton.setOnClickListener {
            sortMealsByPrice()
        }
        val openMapButton: Button = findViewById(R.id.openMapButton)
        openMapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadMeals() {
        CoroutineScope(Dispatchers.IO).launch {
            val meals = mealDatabase.mealDao().getAllMeals() // fetch
            withContext(Dispatchers.Main) {
                mealAdapter.submitList(meals) // update WITH FETCHED
            }
        }
    }

    private fun sortMealsByPrice() {
        CoroutineScope(Dispatchers.IO).launch {
            val sortedMeals = mealDatabase.mealDao().getMealsSortedByPrice() // price sorting
            withContext(Dispatchers.Main) {
                mealAdapter.submitList(sortedMeals)
                Toast.makeText(this@MealSelectorActivity, "Meals sorted by price!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onMealSelected(meal: Meal) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Meal")
        builder.setMessage("Are you sure you want to get this meal? There is no backing up after this selection")

        builder.setPositiveButton("Yes") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                mealDatabase.mealDao().deleteMeal(meal) // removing the meal completely
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MealSelectorActivity,
                        "Meal saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadMeals() // reload agter crud (delete)
                }
            }
        }
        builder.setNegativeButton("No!") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}