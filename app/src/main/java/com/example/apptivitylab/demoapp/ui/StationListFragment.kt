package com.example.apptivitylab.demoapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.MockDataLoader
import com.example.apptivitylab.demoapp.StationsListAdapter
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.fragment_station_list.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment(), StationsListAdapter.StationViewHolder.onSelectStationListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        stationListRecyclerView.layoutManager = layoutManager

        val stationsAdapter = StationsListAdapter()
        stationsAdapter.setStationListener(this)
        stationListRecyclerView.adapter = stationsAdapter
        stationsAdapter.updateDataSet(MockDataLoader.loadStations(context!!))
    }

    override fun onStationSelected(station: Station) {

        val itemDetailsIntent = Intent(context, StationDetailsActivity::class.java)
        itemDetailsIntent.putExtra(getString(R.string.station_item_intent_string), station)

        startActivity(itemDetailsIntent)
    }
}