package com.example.aviato.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aviato.R
import com.example.aviato.item.AirportItem
import kotlinx.android.synthetic.main.airport_item_recycler.view.*

class AirportRecyclerAdapter(
    var airportList: ArrayList<AirportItem>,
    var context: Context
) : RecyclerView.Adapter<AirportRecyclerAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.airport_item_recycler, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AirportRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindItems(airportList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return airportList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(airportItem: AirportItem) {
            itemView.textViewFullName.text=airportItem.fullname
            itemView.textViewCountry.text=airportItem.country
            itemView.textViewIata.text=airportItem.iata
        }
    }

    public fun setList(list: ArrayList<AirportItem>){
        airportList= ArrayList(list)
        notifyDataSetChanged()
    }

}