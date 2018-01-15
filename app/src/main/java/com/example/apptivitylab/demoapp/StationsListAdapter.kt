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
    private var listOfStations: ArrayList<Station> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        return StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station,
                parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stationViewHolder : StationViewHolder = holder as StationViewHolder
        val station : Station = listOfStations.get(position)

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

    class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stationName = itemView.stationCellName
        private val stationBrand = itemView.stationCellBrand

        private var station : Station? = null

        fun setStation(station: Station) {
            this.station = station

            updateViewHolder()
        }

        fun updateViewHolder() {
            stationName.setText(station?.stationName)
            stationBrand.setText(station?.stationBrand)
        }
    }
}