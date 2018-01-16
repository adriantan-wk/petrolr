package com.example.apptivitylab.demoapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.cell_station.view.*
import org.w3c.dom.Text

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfStations: ArrayList<Station> = ArrayList() //Used to hold all stations to be displayed
    private lateinit var stationListener : StationViewHolder.onSelectStationListener

    fun setStationListener(stationListener: StationViewHolder.onSelectStationListener) {
        this.stationListener = stationListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station, //Inflate station cell list xml layout
                parent, false), stationListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stationViewHolder : StationViewHolder = holder as StationViewHolder
        val station : Station = listOfStations[position]

        stationViewHolder.setStation(station) //Assign station from station list to appropriate viewholders
    }

    override fun getItemCount(): Int {
        return listOfStations.size
    }

    fun updateDataSet(stations: ArrayList<Station>) {
        this.listOfStations.clear() //Clear old data
        this.listOfStations = stations //Load new data
        this.notifyDataSetChanged() //Restart recyclerview lifecycle
    }

    class StationViewHolder(itemView: View, handler: onSelectStationListener) //Inner viewholder class
        : RecyclerView.ViewHolder(itemView) {

        interface onSelectStationListener { //Listener interface for station list items
            fun onStationSelected(station: Station)
        }

        private val stationName = itemView.stationCellName
        private val stationBrand = itemView.stationCellBrand

        private var station : Station? = null

        init { //Set listener to viewholder and pass station data to listener implementation in stationlistfragment
            itemView.setOnClickListener({
                handler.onStationSelected(station!!)
            })
        }

        fun setStation(station: Station) { //Set station object data to class member variable
            this.station = station

            updateViewHolder()
        }

        private fun updateViewHolder() { //Set object data to viewholder views
            stationName.text = station?.stationName
            stationBrand.text = station?.stationBrand
        }
    }
}