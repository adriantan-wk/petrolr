package com.example.apptivitylab.demoapp

import android.content.res.Resources
import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R.string.unavailable_string
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.cell_station.view.*
import java.util.*
import kotlin.Comparator

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfStations: ArrayList<Station> = ArrayList()
    private lateinit var stationListener: StationViewHolder.onSelectStationListener

    fun setStationListener(stationListener: StationViewHolder.onSelectStationListener) {
        this.stationListener = stationListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station,
                parent, false), stationListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stationViewHolder: StationViewHolder = holder as StationViewHolder
        val station: Station = listOfStations[position]

        stationViewHolder.setStation(station)
    }

    override fun getItemCount(): Int {
        return listOfStations.size
    }

    fun updateDataSet(stations: ArrayList<Station>, arrangeByDistance: Boolean, userLatLng: LatLng?) {
        this.listOfStations.clear()

        if (arrangeByDistance && userLatLng != null) {
            setDistanceFromUser(stations, userLatLng)
            this.listOfStations = arrangeStationsByDistance(stations)
        }
        else
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
        private val stationDistance = itemView.distanceTextView

        private var station: Station? = null

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
            station?.let {
                stationDistance.text = if (it.distanceFromUser == null) {
                    "Unavailable" //How do you use a string resource outside of an activity?
                } else {
                    "%.2f".format(it.distanceFromUser)
                }
            }
        }
    }

    private fun setDistanceFromUser(stations: ArrayList<Station>, userLatLng: LatLng?) {

        userLatLng?.let {
            val userLocation = Location("Current Location")
            userLocation.latitude = it.latitude
            userLocation.longitude = it.longitude

            for (station in stations) {
                val stationLocation = Location("Destination")

                station.stationLatLng?.apply {
                    stationLocation.latitude = this.latitude
                    stationLocation.longitude = this.longitude
                }

                val distance = userLocation.distanceTo(stationLocation) / 1000
                station.distanceFromUser = distance
            }
        }
    }

    private fun arrangeStationsByDistance(stations: ArrayList<Station>) : ArrayList<Station> {
        Collections.sort(stations) { o1, o2 ->
            val distance1 = o1.distanceFromUser
            val distance2 = o2.distanceFromUser

            if (distance1 != null && distance2 != null)
                (distance1 - distance2).toInt()
            else
                0
        }

        return stations
    }
}

