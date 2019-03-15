package com.example.aviato

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.aviato.item.AirportItem
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var firstDestination: View
    private lateinit var secondDestination: View
    private lateinit var buttonSearch: Button

    private lateinit var firstAirport: AirportItem
    private lateinit var secondAirport: AirportItem

    public val FIRST_DESTINATION = 1111
    public val SECOND_DESTINATION = 2222
    public val REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstDestination = findViewById(R.id.firstDestination)
        secondDestination = findViewById(R.id.secondDestination)
        buttonSearch = findViewById(R.id.buttonSearch)

        firstDestination.setOnClickListener { v -> openSearchActivity(v.id) }
        secondDestination.setOnClickListener { v -> openSearchActivity(v.id) }

        firstAirport = AirportItem("Санкт-Петербург, Россия", "Россия", "LED", LatLng(59.95, 30.316667))
        secondAirport = AirportItem("Барселона, Испания", "Испания", "BCN", LatLng(41.387089, 2.170066))

        fillAirportsViews()

        buttonSearch.setOnClickListener {
            val intent = Intent(this, SearchFlightsActivity::class.java)
            intent.putExtra("first_airport", firstAirport)
            intent.putExtra("second_airport", secondAirport)
            startActivity(intent)
        }
    }

    private fun fillAirportsViews() {
        textViewCityFirst.text = firstAirport.fullname
        textViewFirstIATA.text = firstAirport.iata

        textViewCitySecond.text = secondAirport.fullname
        textViewSecondIATA.text = secondAirport.iata
    }

    private fun openSearchActivity(id: Int) {
        val intent = Intent(this, SearchDestinationActivity::class.java)
        if (id == R.id.firstDestination)
            intent.putExtra("destination", FIRST_DESTINATION)
        else if (id == R.id.secondDestination)
            intent.putExtra("destination", SECOND_DESTINATION)

        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234 && resultCode == 0 && data != null) {
            val airport = data.getParcelableExtra<AirportItem>("airport")
            if (data.getIntExtra("destination", 1111) == FIRST_DESTINATION)
                firstAirport = airport
            else if (data.getIntExtra("destination", 1111) == SECOND_DESTINATION)
                secondAirport = airport

            fillAirportsViews()
        }
    }
}
