package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.R.id.*
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.fragment_station_details.*

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class StationDetailsFragment : Fragment() {
    companion object {
        private val STATION_DETAILS = "station_details"

        fun newInstance(station: Station): StationDetailsFragment {
            val fragment = StationDetailsFragment()

            val args: Bundle = Bundle()
            args.putParcelable(STATION_DETAILS, station)

            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var station : Station

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_station_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            station = it.getParcelable(STATION_DETAILS)
        }

        updateData()
    }

    private fun updateData() { //Update all fields with data from the station object
        stationDetailsFragmentName.text = station.stationName
        stationDetailsFragmentID.text = station.stationID
        stationDetailsFragmentBrand.text = station.stationBrand
        stationDetailsFragmentLatitude.text = station.stationLatLng?.latitude.toString()
        stationDetailsFragmentLongitude.text = station.stationLatLng?.longitude.toString()
    }
}