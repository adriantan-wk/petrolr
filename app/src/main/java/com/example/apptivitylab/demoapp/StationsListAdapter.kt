package com.example.apptivitylab.demoapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.cell_header.view.*
import kotlinx.android.synthetic.main.cell_station.view.*
import java.util.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val STATION: Int = 0
    private val HEADER: Int = 1

    private var stationsAndHeadersList: ArrayList<Any> = ArrayList()
    private lateinit var stationListener: StationViewHolder.onSelectStationListener

    fun setStationListener(stationListener: StationViewHolder.onSelectStationListener) {
        this.stationListener = stationListener
    }

    override fun getItemViewType(position: Int): Int {
        if (stationsAndHeadersList[position] is Station) {
            return STATION
        } else {
            return HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent!!.context)

        var viewHolder: RecyclerView.ViewHolder = when (viewType) {
            STATION -> {
                StationViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.cell_station,
                        parent, false), stationListener)
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
                val station: Station = stationsAndHeadersList[position] as Station

                stationViewHolder.updateStationViewHolder(station)
            }
            else -> {
                val headerViewHolder: HeaderViewHolder = holder as HeaderViewHolder
                val header: String = stationsAndHeadersList[position] as String

                headerViewHolder.updateHeaderViewHolder(header)
            }
        }
    }

    override fun getItemCount(): Int {
        return stationsAndHeadersList.size
    }

    fun updateDataSet(stationsAndHeadersList: ArrayList<Any>) {
        this.stationsAndHeadersList.clear()
        this.stationsAndHeadersList.addAll(stationsAndHeadersList)
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
        private val stationDistanceUnit = itemView.distanceUnitTextView

        private var station: Station? = null

        init {
            itemView.setOnClickListener({
                handler.onStationSelected(station!!)
            })
        }

        fun updateStationViewHolder(station: Station) {
            this.station = station

            stationName.text = station.stationName
            stationBrand.text = station.stationBrand

            if (station.distanceFromUser != null) {
                stationDistance.text = "%.2f".format(station.distanceFromUser)
                stationDistanceUnit.text = itemView.context.getString(R.string.distance_km_away)
                } else {
                    stationDistance.text = ""
                    stationDistanceUnit.text = ""
                }
            }
        }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val headerTitle = itemView.headerTextView

        fun updateHeaderViewHolder(header: String) {
            this.headerTitle.text = header
        }
    }
}


