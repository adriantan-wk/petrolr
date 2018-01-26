package com.example.apptivitylab.demoapp.ui

import android.Manifest
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
import com.example.apptivitylab.demoapp.StationsListAdapter
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_station_list.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment(), StationsListAdapter.StationViewHolder.onSelectStationListener {

    companion object {
        const val USER_EXTRA = "user_object"
        const val STATION_LIST_EXTRA = "station_list"

        fun newInstance(currentUser: User, stations: ArrayList<Station>): StationListFragment {
            val fragment = StationListFragment()

            val args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)
            args.putParcelableArrayList(STATION_LIST_EXTRA, stations)

            fragment.arguments = args
            return fragment
        }
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null
    private var userLatLng: LatLng? = null

    private lateinit var currentUser: User
    private lateinit var stations: ArrayList<Station>

    private val stationsAdapter = StationsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
            this.stations = it.getParcelableArrayList(STATION_LIST_EXTRA)
        }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        stationListRecyclerView.layoutManager = layoutManager

        stationsAdapter.setStationListener(this)
        stationListRecyclerView.adapter = stationsAdapter
        updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
    }

    override fun onStationSelected(station: Station) {

        val stationDetailsIntent = StationDetailsActivity.newLaunchIntent(context!!, station)
        startActivity(stationDetailsIntent)
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

        updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng)
    }

    public fun onUserPreferencesChanged(user: User) {
        this.currentUser = user
        updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng)
    }

    private fun updateAdapterDataSet(stationsAdapter: StationsListAdapter
                                     , stations: ArrayList<Station>, userLatLng: LatLng?) {
        var stationsAndHeadersList = ArrayList<Any>()
        var stationList = ArrayList<Station>()
        stationList.addAll(stations)


        if (userLatLng != null) {
            setDistanceFromUser(stationList, userLatLng)
            stationList = arrangeStationsByDistance(stationList)
            stationsAndHeadersList = arrangeListByPreferences(stationList, currentUser)
        }

        stationsAdapter.updateDataSet(stationsAndHeadersList)
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

    private fun arrangeStationsByDistance(stations: ArrayList<Station>): ArrayList<Station> {
        val distanceSortedStationList = ArrayList<Station>()
        distanceSortedStationList.addAll(stations)

        Collections.sort(distanceSortedStationList) { o1, o2 ->
            val distance1 = o1.distanceFromUser
            val distance2 = o2.distanceFromUser

            if (distance1 != null && distance2 != null)
                (distance1 - distance2).toInt()
            else
                0
        }
        return distanceSortedStationList
    }

    private fun arrangeListByPreferences(stations: ArrayList<Station>, user: User): ArrayList<Any> {

        val stationsWithCorrectPetrolType = ArrayList<Station>()
        val preferredStationList = ArrayList<Station>()
        val arrangedStationsAndHeadersList = ArrayList<Any>()

        val userPreferredPetrolType = user.preferredPetrolType?.petrolID
        val userPreferredBrands: ArrayList<Brand> = user.preferredBrands

        stations.forEach { station ->
            if (station.stationPetrolTypeIDs.contains(userPreferredPetrolType)) {
                stationsWithCorrectPetrolType.add(station)
            }
        }

        stationsWithCorrectPetrolType.forEach { station ->
            if (userPreferredBrands.any { brand ->
                brand.brandName == station.stationBrand
            }) {
                preferredStationList.add(station)
            }
        }

        arrangedStationsAndHeadersList.add(getString(R.string.preferred_stations_string))
        arrangedStationsAndHeadersList.addAll(preferredStationList)

        stationsWithCorrectPetrolType.removeAll(preferredStationList)
        arrangedStationsAndHeadersList.add(getString(R.string.non_preferred_stations_string))
        arrangedStationsAndHeadersList.addAll(stationsWithCorrectPetrolType)

        return arrangedStationsAndHeadersList
    }

    override fun onStop() {
        fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }
}