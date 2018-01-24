package com.example.apptivitylab.demoapp

import android.app.Activity
import android.view.View
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.station_details_infowindow.view.*

/**
 * Created by ApptivityLab on 24/01/2018.
 */

class StationDetailInfoWindowAdapter(private val context: Activity) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker?): View? {
        val view: View = context.layoutInflater.inflate(R.layout.station_details_infowindow, null)

        return if (marker?.title == context.getString(R.string.user_marker_string)) {
            null
        } else {
            val station: Station = marker?.tag as Station

            view.stationNameTextView.text = station.stationName
            view.stationAddressTextView.text = station.stationAddress

            view
        }
    }
}
