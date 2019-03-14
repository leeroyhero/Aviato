package com.example.aviato

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aviato.adapter.AirportRecyclerAdapter
import com.example.aviato.item.AirportItem
import com.example.aviato.retrofit.AirportsLocationService
import com.google.android.gms.maps.model.LatLng
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

class SearchDestinationActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AirportRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_destination)

        backButton = findViewById(R.id.imageViewClose)
        editText = findViewById(R.id.editText)
        recyclerView = findViewById(R.id.recyclerView)

        backButton.setOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter= AirportRecyclerAdapter(ArrayList(), this)
        recyclerView.adapter=adapter

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var text = s.toString().trim();
                if (text.isEmpty())
                    clearRecycler();
                else {
                    searchTextChanged(text);
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        });
    }

    private fun searchTextChanged(text: String) {

        AirportsLocationService.create()
            .getAirportList(text, "ru")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSuccess(t: ResponseBody) {
                    var json = t.string();
                    Log.d("search", "json: " + json)
                    parseRespone(json)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.d("search", "error: " + e.message)
                }

            });
    }

    private fun parseRespone(json: String) {
        val airportList = ArrayList<AirportItem>()
        val jsonArray = JSONObject(json).getJSONArray("cities")

        (0..(jsonArray.length() - 1)).forEach { i ->
            var item = jsonArray.getJSONObject(i)

            var fullname = item.getString("fullname")
            var country = item.getString("country")
            var iata = item.getJSONArray("iata").get(0) as String
            var latLng =
                LatLng(item.getJSONObject("location").getDouble("lat"), item.getJSONObject("location").getDouble("lon"))

            airportList.add(AirportItem(fullname, country, iata, latLng))
        }

        if (airportList.size>0)
            showInRecycler(airportList)
    }

    private fun showInRecycler(airportList: ArrayList<AirportItem>) {
        adapter.setList(airportList)
    }

    private fun clearRecycler() {

    }
}
