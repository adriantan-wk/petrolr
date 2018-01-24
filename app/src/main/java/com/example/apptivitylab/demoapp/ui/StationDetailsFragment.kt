package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.fragment_station_details.*

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class StationDetailsFragment : Fragment() {
    companion object {
        const val STATION_DETAILS = "station_details"

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

        updateView()
    }

    private fun updateView() {
        nameTextView.text = station.stationName
        idTextView.text = station.stationID
        brandTextView.text = station.stationBrand
        latitudeTextView.text = station.stationLatLng?.latitude.toString()
        longitudeTextView.text = station.stationLatLng?.longitude.toString()
    }
}