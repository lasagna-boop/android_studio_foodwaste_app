package com.example.foodwastemanager
//import scope
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//main activity class
class MainActivity : AppCompatActivity() {

    private lateinit var mealDatabase: MealDatabase //connecting everything together
    private lateinit var mealAdapter: MealAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) { //layout oncreate instances
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // db
        mealDatabase = MealDatabase.getDatabase(context = this)

        // RecyclerView + Adapter init
        recyclerView = findViewById(R.id.mealRecyclerView)
        mealAdapter = MealAdapter {}
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mealAdapter

        // loading to rec. view
        loadMeals()

        // saving operations
        val mealNameInput: EditText = findViewById(R.id.mealNameInput)
        val priceInput: EditText = findViewById(R.id.priceInput)
        val restaurantNameInput: EditText = findViewById(R.id.restaurantNameInput)
        val restaurantAddressInput: EditText = findViewById(R.id.restaurantAddressInput) // addressinput track

        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val mealName = mealNameInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val restaurantName = restaurantNameInput.text.toString()
            val restaurantAddress = restaurantAddressInput.text.toString() // address new

            if (mealName.isNotEmpty() && restaurantName.isNotEmpty() && restaurantAddress.isNotEmpty()) {
                val meal = Meal(
                    mealName = mealName,
                    price = price,
                    restaurantName = restaurantName,
                    address = restaurantAddress // save address and check if not empty same with name and price
                )

                // confirmations using toast lib
                AlertDialog.Builder(this).apply {
                    setTitle("Confirm share")
                    setMessage("Do you want to share this meal?")
                    setPositiveButton("Save") { _, _ ->
                        saveMeal(meal)
                        mealNameInput.text.clear()
                        priceInput.text.clear()
                        restaurantNameInput.text.clear()
                        restaurantAddressInput.text.clear() // Clear address input
                    }
                    setNegativeButton("Cancel", null)
                }.show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // screen swaps
        val goToSelectorButton: Button = findViewById(R.id.goToSelectorButton)
        goToSelectorButton.setOnClickListener {
            val intent = Intent(this, MealSelectorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // rewriting meal if anything gets changed
        loadMeals()
    }

    // LOAD meals into db
    private fun loadMeals() {
        CoroutineScope(Dispatchers.IO).launch {
            val meals = mealDatabase.mealDao().getAllMeals() // fetching operations
            withContext(Dispatchers.Main) {
                if (meals.isEmpty()) {
                    findViewById<TextView>(R.id.noMealsTextView).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.mealRecyclerView).visibility = View.GONE
                } else {
                    findViewById<TextView>(R.id.noMealsTextView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.mealRecyclerView).visibility = View.VISIBLE
                }
                mealAdapter.submitList(meals) // recyclerview updating AGAIN!
            }
        }
    }

    // another crud (save)
    private fun saveMeal(meal: Meal) {
        CoroutineScope(Dispatchers.IO).launch {
            val mealId = mealDatabase.mealDao().insertMeal(meal)
            if (mealId > 0) {
                println("Meal successfully inserted with ID: $mealId")
                loadMeals() // not quite sure why are we updating it again but it works
            } else {
                println("Failed to insert meal")
            }
        }
    }
}