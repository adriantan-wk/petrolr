package com.example.apptivitylab.demoapp

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.card_nearest_stations.view.*

/**
 * Created by ApptivityLab on 13/02/2018.
 */

class NearestStationsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val NEAREST_STATION: Int = 0
    private val STATION: Int = 1
    private val SEE_MORE: Int = 2

    private var nearestStations: ArrayList<Station> = ArrayList()
    private var brandList: ArrayList<Brand> = ArrayList()

    private lateinit var nearestStationListener: StationViewHolder.onSelectNearestStationListener
    private lateinit var seeMoreListener: SeeMoreViewHolder.onSelectSeeMoreListener

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NEAREST_STATION -> {
                StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.card_nearest_station,
                        parent, false), this.nearestStationListener)
            }
            STATION -> {
                StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.card_nearest_stations,
                        parent, false), this.nearestStationListener)
            }
            else -> {
                SeeMoreViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.card_see_all,
                        parent, false), this.seeMoreListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (holder?.itemViewType == NEAREST_STATION || holder?.itemViewType == STATION) {
                val stationViewHolder: StationViewHolder = holder as StationViewHolder
                val station: Station = this.nearestStations[holder.adapterPosition]

                val stationLogoID = this.brandList.firstOrNull { brand ->
                    brand.brandName == station.stationBrand
                }?.brandLogo

                stationLogoID?.let {
                    stationViewHolder.updateNearestStationsViewHolder(station, stationLogoID)
                }
            }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> NEAREST_STATION
            position < nearestStations.size -> STATION
            else -> SEE_MORE
        }
    }

    override fun getItemCount(): Int {
        return this.nearestStations.size + 1
    }

    fun setNearestStationListener(nearestStationListener: StationViewHolder.onSelectNearestStationListener) {
        this.nearestStationListener = nearestStationListener
    }

    fun setSeeMoreListener(seeMoreListener: SeeMoreViewHolder.onSelectSeeMoreListener) {
        this.seeMoreListener = seeMoreListener
    }

    fun updateDataSet(nearestStations: ArrayList<Station>, brandList: ArrayList<Brand>) {
        this.nearestStations.clear()
        this.nearestStations.addAll(nearestStations)
        this.brandList = brandList

        this.notifyDataSetChanged()
    }

    class StationViewHolder(itemView: View, handler: onSelectNearestStationListener) : RecyclerView.ViewHolder(itemView) {

        interface onSelectNearestStationListener {
            fun onNearestStationSelected(station: Station)
        }

        private var station: Station? = null

        init {
            itemView.setOnClickListener {
                handler.onNearestStationSelected(station!!)
            }
        }

        fun updateNearestStationsViewHolder(station: Station, stationLogoID: Int) {
            this.station = station

            this.itemView.nearestStationLogo.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources, stationLogoID, null))
            this.itemView.nearestStationName.text = station.stationName

            if (station.distanceFromUser != null) {
                this.itemView.nearestStationDistance.text = "%.2f".format(station.distanceFromUser)
                this.itemView.nearestStationDistanceUnit.text = itemView.context.getString(R.string.distance_km_away)
            } else {
                this.itemView.nearestStationDistance.text = ""
                this.itemView.nearestStationDistanceUnit.text = ""
            }
        }
    }

    class SeeMoreViewHolder(itemView: View, handler: onSelectSeeMoreListener) : RecyclerView.ViewHolder(itemView) {

        interface onSelectSeeMoreListener {
            fun onSeeMoreSelected()
        }

        init {
            itemView.setOnClickListener {
                handler.onSeeMoreSelected()
            }
        }
    }

}