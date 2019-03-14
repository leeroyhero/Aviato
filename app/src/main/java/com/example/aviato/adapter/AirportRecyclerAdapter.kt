package com.example.aviato.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aviato.R
import com.example.aviato.item.AirportItem
import kotlinx.android.synthetic.main.airport_item_recycler.view.*

class AirportRecyclerAdapter(
    var airportList: ArrayList<AirportItem>,
    var activity: Activity,
    var destination: Int
) : RecyclerView.Adapter<AirportRecyclerAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.airport_item_recycler, parent, false)


        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AirportRecyclerAdapter.ViewHolder, position: Int) {
        holder.textViewFullName.text=airportList[position].fullname
        holder.textViewCountry.text=airportList[position].country
        holder.textViewIata.text=airportList[position].iata


        holder.itemView.setOnClickListener { val intent=Intent()
            intent.putExtra("airport",airportList[position])
            intent.putExtra("destination",destination)
            activity.setResult(0, intent)
            activity.finish() }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return airportList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewFullName=itemView.textViewFullName
            val textViewCountry=itemView.textViewCountry
            val textViewIata=itemView.textViewIata

    }

    public fun setList(list: ArrayList<AirportItem>){
        airportList= ArrayList(list)
        notifyDataSetChanged()
    }

}