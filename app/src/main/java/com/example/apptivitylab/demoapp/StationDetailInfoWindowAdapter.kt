package com.example.apptivitylab.demoapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

/**
 * Created by ApptivityLab on 24/01/2018.
 */

class StationDetailInfoWindowAdapter(private val context: Activity) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker?): View {
        val view: View = context.layoutInflater.inflate(R.layout.station_details_infowindow, null)

        val station: Station = marker?.tag as Station

        val stationNameTextView: TextView = view.findViewById(R.id.stationNameTextView)
        val stationAddressTextView: TextView = view.findViewById(R.id.stationAddressTextView)

        stationNameTextView.text = station.stationName
        stationAddressTextView.text = station.stationAddress

        return view
    }
}
