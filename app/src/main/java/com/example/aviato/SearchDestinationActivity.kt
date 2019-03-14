package com.example.aviato

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class SearchDestinationActivity : AppCompatActivity() {
    private lateinit var backButton:ImageView
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_destination)

        backButton=findViewById(R.id.imageViewClose)
        editText=findViewById(R.id.editText)
        recyclerView=findViewById(R.id.recyclerView)
    }
}
