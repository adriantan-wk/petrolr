package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
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
import kotlinx.android.synthetic.main.activity_station_list.*
import kotlinx.android.synthetic.main.fragment_station_list.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment(), StationsListAdapter.StationViewHolder.onSelectStationListener,
        SwipeRefreshLayout.OnRefreshListener {

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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback
    private var locationChangeCounter = 0

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

        this.swipeRefreshLayout.setOnRefreshListener(this)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        this.stationListRecyclerView.layoutManager = layoutManager

        this.stationsAdapter.setStationListener(this)
        this.stationListRecyclerView.adapter = this.stationsAdapter
        this.updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng)

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)
    }

    override fun onStart() {
        super.onStart()

        this.context?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        TrackNearbyFragment.ACCESS_FINE_LOCATION_PERMISSIONS)
            } else {
                this.updateUserLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            TrackNearbyFragment.ACCESS_FINE_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val toast = Toast.makeText(this.context!!, R.string.location_permissions_granted, Toast.LENGTH_SHORT)
                    toast.show()
                    this.updateUserLocation()
                } else {
                    val toast = Toast.makeText(this.context!!, R.string.location_permissions_denied, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            else -> {
            }
        }
    }

    override fun onStationSelected(station: Station) {

        val stationDetailsIntent = StationDetailsActivity.newLaunchIntent(this.context!!, station)
        startActivity(stationDetailsIntent)
    }

    fun onUserPreferencesChanged(user: User) {
        this.currentUser = user

        this.updateUserLocation()
    }

    override fun onRefresh() {
        val request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.interval = 3000
        request.fastestInterval = 1000

        if (ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.createLocationCallBack()
            this.fusedLocationClient.requestLocationUpdates(request, this.locationCallBack, Looper.myLooper())
            Toast.makeText(this.context!!, getString(R.string.updating_location), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this.context!!, getString(R.string.unable_receive_location), Toast.LENGTH_LONG).show()
            this.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun createLocationCallBack() {
        this.locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult?.let {
                    onLocationChanged(it.lastLocation)
                }
            }
        }
    }

    private fun onLocationChanged(location: Location) {
        if (this.locationChangeCounter < 5) {
            if (location.latitude != this.userLatLng?.latitude || location.longitude != this.userLatLng?.longitude) {
                this.locationChangeCounter = 0
                this.fusedLocationClient.removeLocationUpdates(this.locationCallBack)

                this.swipeRefreshLayout.isRefreshing = false
                this.updateUserLocation()
            } else {
                this.locationChangeCounter++
            }
        } else {
            this.fusedLocationClient.removeLocationUpdates(this.locationCallBack)
            this.locationChangeCounter = 0

            this.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(this.context!!, R.string.location_not_changed, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateUserLocation() {
        this.fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                this.userLatLng = LatLng(location.latitude, location.longitude)
                Toast.makeText(this.context!!, R.string.location_updated, Toast.LENGTH_SHORT).show()
                this.updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng)
            }
        }
    }

    private fun updateAdapterDataSet(stationsAdapter: StationsListAdapter
                                     , stations: ArrayList<Station>, userLatLng: LatLng?) {
        var stationsAndHeadersList = ArrayList<Any>()
        var stationList = ArrayList<Station>()
        stationList.addAll(stations)


        if (userLatLng != null) {
            this.setDistanceFromUser(stationList, userLatLng)
            stationList = this.arrangeStationsByDistance(stationList)
            stationsAndHeadersList = this.arrangeListByPreferences(stationList, currentUser)
        }

        stationsAdapter.updateDataSet(stationsAndHeadersList)
    }

    private fun setDistanceFromUser(stations: ArrayList<Station>, userLatLng: LatLng?) {

        userLatLng?.let {
            val userLocation = Location(getString(R.string.current_location))
            userLocation.latitude = it.latitude
            userLocation.longitude = it.longitude

            for (station in stations) {
                val stationLocation = Location(getString(R.string.destination))

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

        arrangedStationsAndHeadersList.add(getString(R.string.preferred_stations))
        arrangedStationsAndHeadersList.addAll(preferredStationList)

        stationsWithCorrectPetrolType.removeAll(preferredStationList)
        arrangedStationsAndHeadersList.add(getString(R.string.non_preferred_stations))
        arrangedStationsAndHeadersList.addAll(stationsWithCorrectPetrolType)

        return arrangedStationsAndHeadersList
    }
}