package com.example.apptivitylab.demoapp

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.cell_header.view.*
import kotlinx.android.synthetic.main.cell_station.view.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val STATION: Int = 0
    private val HEADER: Int = 1

    private var stationsAndHeadersList: ArrayList<Any> = ArrayList()
    private var brandList: ArrayList<Brand> = ArrayList()
    private lateinit var stationListener: StationViewHolder.onSelectStationListener

    fun setStationListener(stationListener: StationViewHolder.onSelectStationListener) {
        this.stationListener = stationListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (this.stationsAndHeadersList[position] is Station) {
            STATION
        } else {
            HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder = when (viewType) {
            STATION -> {
                StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station,
                        parent, false), this.stationListener)
            }
            else -> {
                HeaderViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_header,
                        parent, false))
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            STATION -> {
                val stationViewHolder: StationViewHolder = holder as StationViewHolder
                val station: Station = this.stationsAndHeadersList[position] as Station

                val stationLogo = this.brandList.firstOrNull { brand ->
                    brand.brandID == station.stationBrand
                }?.brandLogo

                stationLogo?.let {
                    stationViewHolder.updateStationViewHolder(station, stationLogo)
                }
            }
            else -> {
                val headerViewHolder: HeaderViewHolder = holder as HeaderViewHolder
                val header: String = this.stationsAndHeadersList[position] as String

                headerViewHolder.updateHeaderViewHolder(header)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.stationsAndHeadersList.size
    }

    fun updateDataSet(stationsAndHeadersList: ArrayList<Any>, brandList: ArrayList<Brand>) {
        this.stationsAndHeadersList.clear()
        this.stationsAndHeadersList.addAll(stationsAndHeadersList)
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

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateHeaderViewHolder(header: String) {
            this.itemView.headerTextView.text = header
        }
    }
}


