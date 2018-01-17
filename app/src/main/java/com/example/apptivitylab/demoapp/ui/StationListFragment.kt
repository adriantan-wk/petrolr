package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.StationsListAdapter
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_station_list.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment() {
    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stations : ArrayList<Station> = ArrayList()
        stations.add(Station(1, "Station 1", "Shell", LatLng(123.1, 123.1)))
        stations.add(Station(2, "Station 2", "BP", LatLng(123.1, 123.1)))

        recyclerView = stationListFragmentRecyclerView

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val stationsAdapter = StationsListAdapter()
        recyclerView.adapter = stationsAdapter
        stationsAdapter.updateDataSet(stations)
    }
}