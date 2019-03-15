package com.example.aviato

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aviato.fragment.PlaneFragment

class SearchFlightsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_flights)

        if (savedInstanceState==null) {
            val arguments = Bundle()
            arguments.putAll(intent.extras)
            val planeFragment = PlaneFragment()
            planeFragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .replace(R.id.contentFragment, planeFragment)
                .commit()
        }
    }
}
