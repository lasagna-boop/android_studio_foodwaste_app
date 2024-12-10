package com.example.foodwastemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MealAdapter(
    private val onMealClick: (Meal) -> Unit // sorting clicks
) : ListAdapter<Meal, MealAdapter.MealViewHolder>(MealDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = getItem(position)
        holder.bind(meal, onMealClick) // Pass the click listener to the ViewHolder (GPT helped here not gonna lie)
    }

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mealNameText: TextView = itemView.findViewById(R.id.mealName)
        private val priceText: TextView = itemView.findViewById(R.id.price)
        private val restaurantNameText: TextView = itemView.findViewById(R.id.restaurantName)

        fun bind(meal: Meal, onMealClick: (Meal) -> Unit) {
            mealNameText.text = meal.mealName
            priceText.text = "$${meal.price}"
            restaurantNameText.text = meal.restaurantName

            itemView.setOnClickListener {
                onMealClick(meal) // click listener
            }
        }
    }

    class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }
    }
}