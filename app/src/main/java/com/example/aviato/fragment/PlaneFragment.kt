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
import android.os.Handler
import com.example.aviato.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.PatternItem
import java.util.Arrays.asList
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import java.util.Arrays.asList
import kotlin.math.PI
import kotlin.math.sin


class PlaneFragment : Fragment(), OnMapReadyCallback {
    private lateinit var firstAirport: AirportItem
    private lateinit var secondAirport: AirportItem
    val PLANE_SPEED = 60
    private var planePosition=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("plane_fr", "savedInstanceState: "+savedInstanceState)
        if (savedInstanceState!=null)
            planePosition=savedInstanceState.getInt("pos")
        else planePosition=0
    }

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

        var route = getRoute2()

        drawLine(googleMap, route)

        Log.d("plane_fr", "start plane")
        startPlane(route, googleMap)

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("pos", planePosition)
        super.onSaveInstanceState(outState)
    }

    private fun getRoute2(): ArrayList<RouteItem> {
        val list = java.util.ArrayList<RouteItem>()

        val startLatLang = firstAirport.latLng
        val endLatLang = secondAirport.latLng

        var latStep = endLatLang.latitude - startLatLang.latitude
        var lonStep = endLatLang.longitude - startLatLang.longitude
        val dist = ((Math.abs(latStep) + Math.abs(lonStep))*6).toInt()
        latStep = latStep / dist
        lonStep = lonStep / dist

        list.add(RouteItem(startLatLang, 0.0))
        for (i in 0..dist) {
            var lat = startLatLang.latitude + latStep * i + 3 * sin((2 * i * PI) / dist)
            var lon = startLatLang.longitude + lonStep * i - 3 * sin((2 * i * PI) / dist)

            if (latStep < 0 && lat < endLatLang.latitude && lonStep < 0 && lon < endLatLang.longitude) break
            if (latStep > 0 && lat > endLatLang.latitude && lonStep < 0 && lon < endLatLang.longitude) break
            if (latStep < 0 && lat < endLatLang.latitude && lonStep > 0 && lon > endLatLang.longitude) break
            if (latStep > 0 && lat > endLatLang.latitude && lonStep > 0 && lon > endLatLang.longitude) break

            list.add(RouteItem(LatLng(lat, lon), 0.0))
        }

        for (i in 1 until list.size - 2) {
            val pathItem = list[i]
            pathItem.angle = (getAngleDegrees(list[i].latLng, list[i+1].latLng))
        }
        return list
    }

    private fun startPlane(path: ArrayList<RouteItem>, googleMap: GoogleMap?) {
        val plane = googleMap?.addMarker(
            MarkerOptions()
                .position(path[0].latLng)
                .rotation(path[0].angle.toFloat())
                .anchor(0.7f, 0.5f)
                .zIndex(10f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plane))
        )

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                planePosition++
                plane?.setPosition(path[planePosition].latLng)
                plane?.rotation = path[planePosition].angle.toFloat()
                if (planePosition < path.size - 3)
                    handler.postDelayed(this, PLANE_SPEED.toLong())
            }
        }, PLANE_SPEED.toLong())
    }

    private fun getAngleDegrees(startLatLang: LatLng, endLatLang: LatLng): Double {
            return Math.atan2(startLatLang.latitude - endLatLang.latitude, startLatLang.longitude - endLatLang.longitude) * (180 / Math.PI) * -1 + 180
    }

    private fun setAirportNameMarker(googleMap: GoogleMap?, airport: AirportItem) {
        val view = LayoutInflater.from(context).inflate(R.layout.airport_map_marker, null)
        view.findViewById<TextView>(R.id.textViewIata).text = airport.iata
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildLayer()

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawableBackground = view.background
        if (drawableBackground != null)
            drawableBackground.draw(canvas)

        view.draw(canvas)

        val options = MarkerOptions()
            .position(airport.latLng)
            .anchor(0.5f, 0.5f)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

        val marker = googleMap?.addMarker(options)
    }


}
