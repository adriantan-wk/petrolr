package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.MockDataLoader
import com.example.apptivitylab.demoapp.StationsListAdapter
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_station_list.*
import java.util.ArrayList

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment(), StationsListAdapter.StationViewHolder.onSelectStationListener {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null
    private var userLatLng: LatLng? = null

    private val stationsAdapter = StationsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        stationListRecyclerView.layoutManager = layoutManager

        stationsAdapter.setStationListener(this)
        stationListRecyclerView.adapter = stationsAdapter

        stationsAdapter.updateDataSet(MockDataLoader.loadStations(context!!), false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
    }

    override fun onStationSelected(station: Station) {

        val itemDetailsIntent = Intent(context, StationDetailsActivity::class.java)
        itemDetailsIntent.putExtra(getString(R.string.station_item_intent_string), station)

        startActivity(itemDetailsIntent)
    }

    private fun startLocationUpdates() {

        this.context?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        TrackNearbyFragment.ACCESS_FINE_LOCATION_PERMISSIONS)
            }
        }

        val request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.interval = 15000
        request.fastestInterval = 10000

        createLocationCallBack()

        fusedLocationClient?.requestLocationUpdates(request, locationCallBack, Looper.myLooper())
    }

    private fun createLocationCallBack() {
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult?.let {
                    onLocationChanged(it.lastLocation)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            TrackNearbyFragment.ACCESS_FINE_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val toast = Toast.makeText(context, R.string.location_permissions_granted_string, Toast.LENGTH_SHORT)
                    toast.show()
                    startLocationUpdates()
                } else {
                    val toast = Toast.makeText(context, R.string.location_permissions_denied_string, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            else -> {
            }
        }
    }

    private fun onLocationChanged(location: Location?) {
        location?.let {
            this.userLatLng = LatLng(it.latitude, it.longitude)
        }

        val stations = MockDataLoader.loadStations(context!!)

        setDistanceFromUser(stations, userLatLng)
        stationsAdapter.updateDataSet(stations, true)
        Toast.makeText(context, R.string.location_updated_string, Toast.LENGTH_SHORT).show()
    }

    private fun setDistanceFromUser(stations: ArrayList<Station>, userLatLng: LatLng?) {

        userLatLng?.let {
            val userLocation = Location(getString(R.string.current_location_string))
            userLocation.latitude = it.latitude
            userLocation.longitude = it.longitude

            for (station in stations) {
                val stationLocation = Location(getString(R.string.destination_string))

                station.stationLatLng?.apply {
                    stationLocation.latitude = this.latitude
                    stationLocation.longitude = this.longitude
                }

                val distance = userLocation.distanceTo(stationLocation) / 1000
                station.distanceFromUser = distance
            }
        }
    }

    override fun onStop() {
        fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }
}