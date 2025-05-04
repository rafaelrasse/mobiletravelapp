package edu.uark.kmbabbit.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class JsonResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json_results)
        val returnButton: ImageView = findViewById(R.id.return_button)

        returnButton.setOnClickListener {
            finish() // Close this activity and return to MainPageActivity
        }

        val hotelContainer: LinearLayout = findViewById(R.id.hotels_container)
        val restaurantContainer: LinearLayout = findViewById(R.id.restaurants_container)

        val hotelJson = intent.getStringExtra("hotels_json") ?: "[]"
        val restaurantJson = intent.getStringExtra("restaurants_json") ?: "[]"

        // Parse and display hotels
        displayPlaces(hotelJson, hotelContainer)

        // Parse and display restaurants
        displayPlaces(restaurantJson, restaurantContainer)
    }

    private fun displayPlaces(jsonData: String, container: LinearLayout) {
        val jsonArray = JSONArray(jsonData)

        for (i in 0 until jsonArray.length()) {
            val placeObject = jsonArray.getJSONObject(i)

            val name = placeObject.optString("name", "Unknown Name")
            val address = placeObject.optString("address", "Unknown Address")
            val website = placeObject.optString("website", "No Website Available")
            val contact = placeObject.optJSONObject("contact")
            val phone = contact?.optString("phone", "No Phone Available")
            val email = contact?.optString("email", "No Email Available")

            val placeCard = layoutInflater.inflate(R.layout.item_place_card, container, false)

            val nameTextView: TextView = placeCard.findViewById(R.id.place_name)
            val addressTextView: TextView = placeCard.findViewById(R.id.place_address)
            val websiteTextView: TextView = placeCard.findViewById(R.id.place_website)
            val phoneTextView: TextView = placeCard.findViewById(R.id.place_phone)
            val emailTextView: TextView = placeCard.findViewById(R.id.place_email)

            nameTextView.text = "Name: $name"
            addressTextView.text = "Address: $address"
            websiteTextView.text = "Website: $website"
            phoneTextView.text = "Phone: $phone"
            emailTextView.text = "Email: $email"

            container.addView(placeCard)
        }
    }

}
