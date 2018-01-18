package com.example.apptivitylab.demoapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.cell_station.view.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfStations: ArrayList<Station> = ArrayList()
    private lateinit var stationListener : StationViewHolder.onSelectStationListener

    fun setStationListener(stationListener: StationViewHolder.onSelectStationListener) {
        this.stationListener = stationListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station,
                parent, false), stationListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stationViewHolder : StationViewHolder = holder as StationViewHolder
        val station : Station = listOfStations[position]

        stationViewHolder.setStation(station)
    }

    override fun getItemCount(): Int {
        return listOfStations.size
    }

    fun updateDataSet(stations: ArrayList<Station>) {
        this.listOfStations.clear()
        this.listOfStations = stations
        this.notifyDataSetChanged()
    }

    class StationViewHolder(itemView: View, handler: onSelectStationListener)
        : RecyclerView.ViewHolder(itemView) {

        interface onSelectStationListener {
            fun onStationSelected(station: Station)
        }

        private val stationName = itemView.nameTextView
        private val stationBrand = itemView.brandTextView

        private var station : Station? = null

        init {
            itemView.setOnClickListener({
                handler.onStationSelected(station!!)
            })
        }

        fun setStation(station: Station) {
            this.station = station

            updateViewHolder()
        }

        private fun updateViewHolder() {
            stationName.text = station?.stationName
            stationBrand.text = station?.stationBrand
        }
    }
}