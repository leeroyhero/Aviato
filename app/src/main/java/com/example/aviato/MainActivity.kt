package com.example.aviato

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var firstDestination: View
    private lateinit var secondDestination: View
    private lateinit var buttonSearch: Button

    public val FIRST_DESTINATION=1111
    public val SECOND_DESTINATION=2222
    public val REQUEST_CODE=1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstDestination=findViewById(R.id.firstDestination)
        secondDestination=findViewById(R.id.secondDestination)
        buttonSearch=findViewById(R.id.buttonSearch)

        firstDestination.setOnClickListener{v->openSearchActivity(v.id)}
        secondDestination.setOnClickListener{v->openSearchActivity(v.id)}
    }

    private fun openSearchActivity(id: Int) {
        val intent = Intent(this, SearchDestinationActivity::class.java)
        if (id==R.id.firstDestination)
            intent.putExtra("destination", FIRST_DESTINATION)
         else if (id==R.id.secondDestination)
            intent.putExtra("destination", SECOND_DESTINATION)

            startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
}
