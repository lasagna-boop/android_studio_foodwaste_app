package com.example.foodwastemanager

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mealDatabase: MealDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mealDatabase = MealDatabase.getDatabase(this)

        // map init (all coming from google cloud guides)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // reusing the button though swipe works.. sometimes
        val goBackButton: Button = findViewById(R.id.goBackButton)
        goBackButton.setOnClickListener {
            finish() // jump to previous screen
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadRestaurantLocations()
    }

    private fun loadRestaurantLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            val meals = mealDatabase.mealDao().getAllMeals() // fetching
            withContext(Dispatchers.Main) {
                if (meals.isEmpty()) {
                    Toast.makeText(this@MapActivity, "No meals to display on the map.", Toast.LENGTH_SHORT).show()
                } else {
                    for (meal in meals) {
                        val address = meal.address // replacing again here (with address though)
                        if (!address.isNullOrEmpty()) {
                            addMarkerForAddress(address, meal.mealName)
                        }
                    }
                }
            }
        }
    }
    //marker features from Geocoder extention (also from google cloud guides seems easy enough to use but super powerfull)
    private fun addMarkerForAddress(address: String, title: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)

                // marker edited
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(address)
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            } else {
                Toast.makeText(this, "Could not find location for $title.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error finding location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}