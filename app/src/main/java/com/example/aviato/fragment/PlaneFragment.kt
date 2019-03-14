package com.example.aviato.fragment


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.aviato.item.AirportItem
import com.example.aviato.item.RouteItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*
import android.R.color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.PatternItem
import java.util.Arrays.asList
import com.google.android.gms.maps.model.PolylineOptions



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
    val STEP=0.01f
    val PLANE_SPEED=50


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

        var route=getRoute()

        drawLine(googleMap, route)

    }

    private fun drawLine(googleMap: GoogleMap?, route: ArrayList<RouteItem>) {
        val line = java.util.ArrayList<LatLng>()
        for (pathItem in route)
            line.add(pathItem.latLng)

        val polylineOptions = PolylineOptions()
            .addAll(line)

            .geodesic(false)
            .width(10f)
            .color(resources.getColor(R.color.colorPrimary))
        val polyline = googleMap?.addPolyline(polylineOptions)
        polyline?.pattern = Arrays.asList(Dot(), Gap(20f));

        polyline?.jointType = JointType.DEFAULT

    }

    private fun getRoute(): ArrayList<RouteItem> {
        val list = java.util.ArrayList<RouteItem>()

        val startLatLang=firstAirport.latLng
        val endLatLang=secondAirport.latLng

        val length = Math.sqrt(Math.pow(startLatLang.latitude - endLatLang.latitude, 2.0
            ) + Math.pow(startLatLang.longitude - endLatLang.longitude, 2.0))
       // val angle = 0.0

        val angle = getAngle(startLatLang, endLatLang)

        val sin = Math.sin(angle)
        val cos = Math.cos(angle)


        list.add(RouteItem(startLatLang, 0.0))
        run {
            var i = 0.0
            while (i < length) {
                val x = if (startLatLang.longitude - endLatLang.longitude > 0) i * -1 else i

                val latitude = (4 * Math.sin(x * 2.0 * Math.PI / length))

                val newLat = (x * sin + latitude * cos + startLatLang.latitude)
                val newLon = (x * cos + latitude * sin + startLatLang.longitude)

                val latLng = LatLng(newLat, newLon)
                list.add(RouteItem(latLng, 0.0))
                i += STEP
            }
        }
        // list.add(new PathItem(endLatLang,0));

        for (i in 1 until list.size - 2) {
            val pathItem = list[i]
            pathItem.angle=(getAngleDegrees(list[i - 1].latLng, list[i + 1].latLng))
        }

        return list
    }

    private fun getAngle(startLatLang: LatLng, endLatLang: LatLng): Double {
        return ((endLatLang.latitude - startLatLang.latitude) / (endLatLang.longitude - startLatLang.longitude))
      //  return ((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude))
    }

    private fun getAngleDegrees(startLatLang: LatLng, endLatLang: LatLng): Double {
         if (startLatLang.longitude > endLatLang.longitude)
             return     ((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude) * (180 / Math.PI)) * -1 + 180
        else
        return ((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude) * (180 / Math.PI)) * -1
        // return (float) Math.tan(((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude))/(Math.PI*180));
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
