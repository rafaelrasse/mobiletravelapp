package edu.uark.kmbabbit.finalproject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)  // Set your layout here for MainPageActivity

        val itemLayout: LinearLayout = findViewById(R.id.layoutCategories)
        val itemLayoutRecommended: LinearLayout = findViewById(R.id.layoutRecommended)

        val picList = arrayOf(
            R.drawable.baseline_airplane_ticket_24,
            R.drawable.baseline_hotel_24,
            R.drawable.baseline_directions_bus_24,
            R.drawable.baseline_restaurant_24,
            R.drawable.baseline_attractions_24
        )



        val titleList = arrayOf(
            "Flights", "Hotels", "Transportation", "Restaurants", "Attractions"
        )

        val picsRecommended = arrayOf(
            R.drawable.japan,
            R.drawable.iceland,
            R.drawable.santorini,
            R.drawable.rome,
            R.drawable.amalficoast,
            R.drawable.cancun
        )

        val cityRecommended = arrayOf("Tokyo", "Reykjavík", "Santorini", "Rome", "Amalfi Coast", "Cancun")

        for (i in picList.indices) {
            val view = layoutInflater.inflate(R.layout.item_categories, itemLayout, false)
            val itemPic: ImageView = view.findViewById(R.id.item_image)
            val itemTitle: TextView = view.findViewById(R.id.item_name)
            itemPic.setImageResource(picList[i])
            itemTitle.text = titleList[i]

            // Add click listener for interaction
            view.setOnClickListener {
                Log.d(TAG, "Category clicked: ${titleList[i]}")
                // Create the intent and start the DetailActivity when a category is clicked
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("categoryName", titleList[i])
                startActivity(intent)
            }

            itemLayout.addView(view)
        }


        for (i in picsRecommended.indices) {
            val view = layoutInflater.inflate(R.layout.item_recommended, itemLayoutRecommended, false)
            val itemRecommended: ImageView = view.findViewById(R.id.item_recommended_image)
            val itemCity: TextView = view.findViewById(R.id.item_city)
            itemRecommended.setImageResource(picsRecommended[i])
            itemCity.text = cityRecommended[i]
            itemLayoutRecommended.addView(view)
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "SECRETKEY")
        }

        val autocompleteFragment = (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment).setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")

                place.latLng?.let { latLng ->
                    // Perform API request with latitude and longitude
                    fetchNearbyPlaces(latLng.latitude, latLng.longitude)
                }
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    private fun fetchNearbyPlaces(latitude: Double, longitude: Double) {
        val client = OkHttpClient()

        // URLs for hotels and restaurants
        val hotelUrl = "https://api.geoapify.com/v2/places?categories=accommodation.hotel&filter=circle:$longitude,$latitude,5000&limit=5&apiKey=SECRETKEY"
        val restaurantUrl = "https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=circle:$longitude,$latitude,5000&limit=5&apiKey=SECRETKEY"

        val hotelRequest = Request.Builder().url(hotelUrl).build()
        val restaurantRequest = Request.Builder().url(restaurantUrl).build()

        // Fetch hotels
        client.newCall(hotelRequest).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Hotel API Request Failed: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val hotelData = response.body?.string()
                    if (hotelData != null) {
                        fetchRestaurants(client, restaurantRequest, latitude, longitude, hotelData)
                    }
                } else {
                    Log.e(TAG, "Hotel API Request Failed with status code: ${response.code}")
                }
            }
        })
    }

    private fun fetchRestaurants(
        client: OkHttpClient,
        restaurantRequest: Request,
        latitude: Double,
        longitude: Double,
        hotelData: String
    ) {
        // Fetch restaurants
        client.newCall(restaurantRequest).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Restaurant API Request Failed: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val restaurantData = response.body?.string()
                    if (restaurantData != null) {
                        parseNearbyPlaces(hotelData, restaurantData)
                    }
                } else {
                    Log.e(TAG, "Restaurant API Request Failed with status code: ${response.code}")
                }
            }
        })
    }

    private fun parseNearbyPlaces(hotelData: String, restaurantData: String) {
        val hotelJsonArray = filterPlaces(hotelData)
        val restaurantJsonArray = filterPlaces(restaurantData)

        val intent = Intent(this, JsonResultsActivity::class.java)
        intent.putExtra("hotels_json", hotelJsonArray.toString())
        intent.putExtra("restaurants_json", restaurantJsonArray.toString())
        startActivity(intent)
    }

    private fun filterPlaces(jsonResponse: String): JSONArray {
        val outputJsonArray = JSONArray()
        try {
            val jsonObject = JSONObject(jsonResponse)
            val features = jsonObject.getJSONArray("features")

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val properties = feature.getJSONObject("properties")

                val name = properties.optString("name", "Unknown Name")
                val formatted = properties.optString("formatted", "Unknown Address")
                val website = properties.optString("website", "No Website Available")

                val contact = properties.optJSONObject("contact")
                val phone = contact?.optString("phone", "No Phone Available")
                val email = contact?.optString("email", "No Email Available")

                val filteredPlace = JSONObject()
                filteredPlace.put("name", name)
                filteredPlace.put("address", formatted)
                filteredPlace.put("website", website)

                val contactDetails = JSONObject()
                contactDetails.put("phone", phone)
                contactDetails.put("email", email)
                filteredPlace.put("contact", contactDetails)

                outputJsonArray.put(filteredPlace)
            }
        } catch (e: Exception) {
            Log.e("JSONParsing", "Error parsing and filtering JSON: ${e.message}")
        }
        return outputJsonArray
    }


}