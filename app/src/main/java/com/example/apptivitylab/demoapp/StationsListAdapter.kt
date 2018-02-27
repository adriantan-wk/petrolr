package com.example.apptivitylab.demoapp

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.cell_station.view.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var stationList: ArrayList<Station> = ArrayList()
    private var brandList: ArrayList<Brand> = ArrayList()
    private lateinit var stationListener: StationViewHolder.onSelectStationListener

    fun setStationListener(stationListener: StationViewHolder.onSelectStationListener) {
        this.stationListener = stationListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station,
                parent, false), this.stationListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stationViewHolder: StationViewHolder = holder as StationViewHolder
        val station: Station = this.stationList[position] as Station

        val stationLogo = this.brandList.firstOrNull { brand ->
            brand.brandID == station.stationBrand
        }?.brandLogo

        stationLogo?.let {
            stationViewHolder.updateStationViewHolder(station, stationLogo)
        }
    }

    override fun getItemCount(): Int {
        return this.stationList.size
    }

    fun updateDataSet(stationList: ArrayList<Station>, brandList: ArrayList<Brand>) {
        this.stationList.clear()
        this.stationList.addAll(stationList)
        this.brandList = brandList

        this.notifyDataSetChanged()
    }

    class StationViewHolder(itemView: View, handler: onSelectStationListener)
        : RecyclerView.ViewHolder(itemView) {

        interface onSelectStationListener {
            fun onStationSelected(station: Station)
        }

        private var station: Station? = null

        init {
            itemView.setOnClickListener({
                handler.onStationSelected(station!!)
            })
        }

        fun updateStationViewHolder(station: Station, stationLogoID: Int) {
            this.station = station

            this.itemView.logoImageView.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources, stationLogoID, null))
            this.itemView.nameTextView.text = station.stationName

            if (station.distanceFromUser != null) {
                this.itemView.distanceTextView.text = "%.2f".format(station.distanceFromUser)
                this.itemView.distanceUnitTextView.text = itemView.context.getString(R.string.distance_km_away)
            } else {
                this.itemView.distanceTextView.text = ""
                this.itemView.distanceUnitTextView.text = ""
            }
        }
    }
}


