package com.example.aviato.fragment


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import com.example.aviato.R
import com.example.aviato.item.AirportItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PlaneFragment : Fragment(), OnMapReadyCallback {
    private lateinit var firstAirport: AirportItem
    private lateinit var secondAirport: AirportItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        firstAirport = arguments?.getParcelable("first_airport") as AirportItem
        secondAirport = arguments?.getParcelable("second_airport") as AirportItem
        return inflater.inflate(R.layout.fragment_plane, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val builder = LatLngBounds.Builder()
        builder.include(firstAirport.latLng)
        builder.include(secondAirport.latLng)
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, padding)
        googleMap?.moveCamera(cameraUpdate)

        setAirportNameMarker(googleMap, firstAirport)
        setAirportNameMarker(googleMap, secondAirport)
    }

    private fun setAirportNameMarker(googleMap: GoogleMap?, airport: AirportItem) {
        val view=LayoutInflater.from(context).inflate(R.layout.airport_map_marker, null)
        view.findViewById<TextView>(R.id.textViewIata).text=airport.iata
     /*   val layoutParams=ConstraintLayout.LayoutParams(30,30)
        view.layoutParams=layoutParams*/
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0,0, view.measuredWidth, view.measuredHeight)
        view.buildLayer()

        val bitmap=Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawableBackground=view.background
        if (drawableBackground!=null)
            drawableBackground.draw(canvas)

        view.draw(canvas)

        val options = MarkerOptions()
            .position(airport.latLng)
            .anchor(0.5f, 0.5f)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

        val marker= googleMap?.addMarker(options)
    }


}
