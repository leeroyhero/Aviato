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
    val STEP = 0.01f
    val PLANE_SPEED = 70


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

    private fun getRoute2(): ArrayList<RouteItem> {
        val list = java.util.ArrayList<RouteItem>()

        val startLatLang = firstAirport.latLng
        val endLatLang = secondAirport.latLng


        val length = Math.sqrt(
            Math.pow(
                startLatLang.latitude - endLatLang.latitude, 2.0
            ) + Math.pow(startLatLang.longitude - endLatLang.longitude, 2.0)
        )
        // val angle = 0.0

        var latStep = endLatLang.latitude - startLatLang.latitude
        var lonStep = endLatLang.longitude - startLatLang.longitude
        val dist = ((Math.abs(latStep) + Math.abs(lonStep))*5).toInt()
        latStep = latStep / dist
        lonStep = lonStep / dist

        Log.d("plane_fr", "start: "+startLatLang+" end: "+endLatLang)
        Log.d("plane_fr", "dist: "+dist)


        list.add(RouteItem(startLatLang, 0.0))
        for (i in 0..dist){
            var lat = startLatLang.latitude+ latStep*i + 3*sin((2*i* PI)/dist)
            var lon = startLatLang.longitude + lonStep*i -3*sin((2*i* PI)/dist)

            Log.d("plane_fr", "lat: "+lat+" lon: "+lon)

            if (latStep < 0 && lat < endLatLang.latitude && lonStep < 0 && lon < endLatLang.longitude) break
            if (latStep > 0 && lat > endLatLang.latitude && lonStep < 0 && lon < endLatLang.longitude) break
            if (latStep < 0 && lat < endLatLang.latitude && lonStep > 0 && lon > endLatLang.longitude) break
            if (latStep > 0 && lat > endLatLang.latitude && lonStep > 0 && lon > endLatLang.longitude) break

            list.add(RouteItem(LatLng(lat, lon), 0.0))
        }

        for (i in 1 until list.size - 2) {
            val pathItem = list[i]
            pathItem.angle = (getAngleDegrees(list[i - 1].latLng, list[i + 1].latLng))
        }
        return list
    }

    private fun getRoute(): ArrayList<RouteItem> {
        val list = java.util.ArrayList<RouteItem>()

        val startLatLang = firstAirport.latLng
        val endLatLang = secondAirport.latLng

        val length = Math.sqrt(
            Math.pow(
                startLatLang.latitude - endLatLang.latitude, 2.0
            ) + Math.pow(startLatLang.longitude - endLatLang.longitude, 2.0)
        )
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
            pathItem.angle = (getAngleDegrees(list[i - 1].latLng, list[i + 1].latLng))
        }
        return list
    }

    private fun startPlane(path: ArrayList<RouteItem>, googleMap: GoogleMap?) {
        val plane = googleMap?.addMarker(
            MarkerOptions()
                .position(path[0].latLng)
                .rotation(path[0].angle.toFloat())
                .anchor(0.7f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plane))
        )

        val pos = intArrayOf(1)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                pos[0]++
                plane?.setPosition(path[pos[0]].latLng)
                plane?.rotation = path[pos[0]].angle.toFloat()
                if (pos[0] < path.size - 3)
                    handler.postDelayed(this, PLANE_SPEED.toLong())
            }
        }, PLANE_SPEED.toLong())
    }

    private fun getAngle(startLatLang: LatLng, endLatLang: LatLng): Double {
        return ((endLatLang.latitude - startLatLang.latitude) / (endLatLang.longitude - startLatLang.longitude))
        //  return ((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude))
    }

    private fun getAngleDegrees(startLatLang: LatLng, endLatLang: LatLng): Double {
        if (startLatLang.longitude > endLatLang.longitude)
            return ((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude) * (180 / Math.PI)) * -1 + 180
        else
            return ((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude) * (180 / Math.PI)) * -1
        // return (float) Math.tan(((startLatLang.latitude - endLatLang.latitude) / (startLatLang.longitude - endLatLang.longitude))/(Math.PI*180));
    }

    private fun setAirportNameMarker(googleMap: GoogleMap?, airport: AirportItem) {
        val view = LayoutInflater.from(context).inflate(R.layout.airport_map_marker, null)
        view.findViewById<TextView>(R.id.textViewIata).text = airport.iata
        /*   val layoutParams=ConstraintLayout.LayoutParams(30,30)
           view.layoutParams=layoutParams*/
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
