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
import com.example.apptivitylab.demoapp.StationLoader
import com.example.apptivitylab.demoapp.StationsListAdapter
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.fragment_station_list.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment(), StationsListAdapter.StationViewHolder.onSelectStationListener {
    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = stationListFragmentRecyclerView

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val stationsAdapter = StationsListAdapter()
        stationsAdapter.setStationListener(this) //Make stationlistfragment the listener for the station list items
        recyclerView.adapter = stationsAdapter
        stationsAdapter.updateDataSet(StationLoader.loadStations(context!!)) //Load fake station data from text file
    }

    override fun onStationSelected(station: Station) { //Implementation of station list item listener from inner viewholder class

        val itemDetailsIntent = Intent(context, StationDetailsActivity::class.java)
        itemDetailsIntent.putExtra("Selected Station", station) //Pass selected station object

        startActivity(itemDetailsIntent) //Move to station details screen
    }
}