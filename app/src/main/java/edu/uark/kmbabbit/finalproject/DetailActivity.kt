package edu.uark.kmbabbit.finalproject

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val categoryName = intent.getStringExtra("categoryName") ?: "Default Category"
        val categoryNameTextView: TextView = findViewById(R.id.categoryNameTextView)
        categoryNameTextView.text = categoryName

        val sectionTitle: TextView = findViewById(R.id.sectionTitle)
        val detailsContainer: LinearLayout = findViewById(R.id.detailsContainer)
        val attractionsContainer: LinearLayout = findViewById(R.id.attractionsContainer)
        val comingSoonTextView: TextView = findViewById(R.id.comingSoonText) // Assuming you have this TextView in your XML

// Set title and populate based on category
        if (categoryName == "Restaurants") {
            sectionTitle.text = "Top 5 Restaurants in the World"

            val restaurants = getTopRestaurants()
            for (restaurant in restaurants) {
                val restaurantView = layoutInflater.inflate(R.layout.item_restaurant, detailsContainer, false)
                val imageView: ImageView = restaurantView.findViewById(R.id.restaurantImage)
                val nameView: TextView = restaurantView.findViewById(R.id.restaurantName)
                val locationView: TextView = restaurantView.findViewById(R.id.restaurantLocation)
                val ratingBar: RatingBar = restaurantView.findViewById(R.id.restaurantRatingBar)

                imageView.setImageResource(restaurant.imageRes)
                nameView.text = restaurant.name
                locationView.text = restaurant.location
                ratingBar.rating = restaurant.rating.toFloat()

                detailsContainer.addView(restaurantView)
            }
        } else if (categoryName == "Attractions") {
            sectionTitle.text = "Top 5 Attractions in the World"

            val attractions = getTopAttractions()
            for (attraction in attractions) {
                val attractionView = layoutInflater.inflate(R.layout.item_attraction, attractionsContainer, false)
                val imageView: ImageView = attractionView.findViewById(R.id.attractionImage)
                val nameView: TextView = attractionView.findViewById(R.id.attractionName)
                val descriptionView: TextView = attractionView.findViewById(R.id.attractionDescription)
                val ratingBar: RatingBar = attractionView.findViewById(R.id.attractionRatingBar)

                imageView.setImageResource(attraction.imageRes)
                nameView.text = attraction.name
                descriptionView.text = attraction.description
                ratingBar.rating = attraction.rating.toFloat()

                attractionsContainer.addView(attractionView)
            }
        } else {
            // For categories other than "Restaurants" and "Attractions"
            sectionTitle.text = "Coming Soon"
            comingSoonTextView.visibility = View.VISIBLE
        }



        // Handle back button click
        val returnButton: ImageView = findViewById(R.id.return_button)
        returnButton.setOnClickListener { finish() }
    }

    // Get the top 5 restaurants (hardcoded data)
    private fun getTopRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant("Osteria Francescana", "Modena, Italy", R.drawable.osteria_francescana, 5.0),
            Restaurant("El Celler de Can Roca", "Girona, Spain", R.drawable.el_celler_de_can_roca, 4.8),
            Restaurant("Mirazur", "Menton, France", R.drawable.mirazur, 4.9),
            Restaurant("Noma", "Copenhagen, Denmark", R.drawable.noma, 4.7),
            Restaurant("Eleven Madison Park", "New York City, USA", R.drawable.eleven_madison_park, 4.6)
        )
    }

    // Get the top 5 attractions (hardcoded data)
    private fun getTopAttractions(): List<Attraction> {
        return listOf(
            Attraction("Eiffel Tower", "A wrought-iron lattice tower in Paris.", R.drawable.eiffel_tower, 4.7),
            Attraction("Great Wall of China", "A series of fortifications made of stone.", R.drawable.great_wall_of_china, 4.8),
            Attraction("Machu Picchu", "An Incan citadel in Peru.", R.drawable.machu_picchu, 4.9),
            Attraction("Colosseum", "An ancient amphitheater in Rome.", R.drawable.colosseum, 4.9),
            Attraction("Taj Mahal", "A white marble mausoleum in India.", R.drawable.taj_mahal, 4.8)
        )
    }

    // Data class for Restaurant
    data class Restaurant(val name: String, val location: String, val imageRes: Int, val rating: Double)

    // Data class for Attraction
    data class Attraction(val name: String, val description: String, val imageRes: Int, val rating: Double)
}
