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

    private lateinit var stationInfoImage : ImageView
    private lateinit var stationInfoName : TextView
    private lateinit var stationInfoID : TextView
    private lateinit var stationInfoBrand : TextView
    private lateinit var stationInfoLong : TextView
    private lateinit var stationInfoLat : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_station_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stationInfoImage = stationDetailsFragmentImage
        stationInfoName = stationDetailsFragmentName
        stationInfoID = stationDetailsFragmentID
        stationInfoBrand = stationDetailsFragmentBrand
        stationInfoLat = stationDetailsFragmentLatitude
        stationInfoLong = stationDetailsFragmentLongitude

        station = arguments!!.getParcelable(STATION_DETAILS)
        updateData()
    }

    private fun updateData() { //Update all fields with data from the station object
        stationInfoName.text = station.stationName
        stationInfoID.text = station.stationID
        stationInfoBrand.text = station.stationBrand
        stationInfoLat.text = station.stationLatLng?.latitude.toString()
        stationInfoLong.text = station.stationLatLng?.longitude.toString()
    }
}