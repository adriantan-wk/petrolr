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
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.StationsListAdapter
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import com.example.apptivitylab.demoapp.ui.StationListFragment.Companion.NON_PREFERRED
import com.example.apptivitylab.demoapp.ui.StationListFragment.Companion.PREFERRED
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_station_list.*

/**
 * Created by ApptivityLab on 15/01/2018.
 */

class StationListFragment : Fragment(), StationsListAdapter.StationViewHolder.onSelectStationListener,
        SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val PREFERRED = 0
        const val NON_PREFERRED = 1
        const val USER_EXTRA = "user_object"

        fun newInstance(currentUser: User): StationListFragment {
            val fragment = StationListFragment()

            val args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)

            fragment.arguments = args
            return fragment
        }
    }

    private var hasPreferredStations: Boolean = true
    private var hasNonPreferredStations: Boolean = true
    private var currentMode = PREFERRED

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
        }

        this.stations = StationController.stationList

        this.swipeRefreshLayout.setOnRefreshListener(this)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        this.stationListRecyclerView.layoutManager = layoutManager

        this.currentMode = PREFERRED
        this.selectStationButton(PREFERRED)

        this.stationsAdapter.setStationListener(this)
        this.stationListRecyclerView.adapter = this.stationsAdapter
        this.changeDisplayedStationList(this.currentMode)

        this.preferredStationsButton.setOnClickListener {
            this.changeDisplayedStationList(PREFERRED)
        }

        this.nonPreferredStationsButton.setOnClickListener {
            this.changeDisplayedStationList(NON_PREFERRED)
        }

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)
    }

    private fun selectStationButton(choice: Int) {
        when (choice) {
            PREFERRED -> {
                this.preferredStationsButton.setBackgroundResource(R.drawable.button_rounded)
                this.preferredStationsButton.setTextColor(ContextCompat.getColor(this.context!!, android.R.color.white))

                this.nonPreferredStationsButton.setBackgroundResource(android.R.color.transparent)
                this.nonPreferredStationsButton.setTextColor(ContextCompat.getColor(this.context!!, R.color.colorAccent))
            }
            NON_PREFERRED -> {
                this.nonPreferredStationsButton.setBackgroundResource(R.drawable.button_rounded)
                this.nonPreferredStationsButton.setTextColor(ContextCompat.getColor(this.context!!, android.R.color.white))

                this.preferredStationsButton.setBackgroundResource(android.R.color.transparent)
                this.preferredStationsButton.setTextColor(ContextCompat.getColor(this.context!!, R.color.colorAccent))
            }
        }
    }

    private fun changeDisplayedStationList(currentMode: Int) {
        var hasStations = true
        var stationTypeString = String()

        this.selectStationButton(currentMode)
        this.currentMode = currentMode
        this.updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng, this.currentMode)

        when (currentMode) {
            PREFERRED -> {
                hasStations = this.hasPreferredStations
                stationTypeString = getString(R.string.no_stations, getString(R.string.preferred))
            }
            NON_PREFERRED -> {
                hasStations = this.hasNonPreferredStations
                stationTypeString = getString(R.string.no_stations, getString(R.string.non_preferred))
            }
        }

        if (hasStations) {
            this.swipeRefreshLayout.visibility = View.VISIBLE
            this.noStationsTextView.visibility = View.GONE
        } else {
            this.swipeRefreshLayout.visibility = View.GONE
            this.noStationsTextView.text = stationTypeString
            this.noStationsTextView.visibility = View.VISIBLE
        }
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

        val stationDetailsIntent = StationDetailsActivity.newLaunchIntent(this.context!!, station, BrandController.brandList)
        startActivity(stationDetailsIntent)
    }

    fun onUserPreferencesChanged(user: User) {
        this.currentUser = user

        this.changeDisplayedStationList(this.currentMode)
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
            }

            this.updateAdapterDataSet(this.stationsAdapter, this.stations, this.userLatLng, this.currentMode)
        }
    }

    private fun updateAdapterDataSet(stationsAdapter: StationsListAdapter, stations: ArrayList<Station>,
                                     userLatLng: LatLng?, currentMode: Int) {
        var stationList = ArrayList<Station>()
        var finalStationList: ArrayList<Station>
        stationList.addAll(stations)


        if (userLatLng != null) {
            this.setDistanceFromUser(stationList, userLatLng)
            stationList = this.arrangeStationsByDistance(stationList)
        }

        finalStationList = this.createStationList(stationList, this.currentUser, currentMode)
        stationsAdapter.updateDataSet(finalStationList, BrandController.brandList)
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

        distanceSortedStationList.sortBy { it.distanceFromUser }

        return distanceSortedStationList
    }

    private fun createStationList(stations: ArrayList<Station>, user: User, currentMode: Int): ArrayList<Station> {

        val stationsWithPreferredPetrolType = ArrayList<Station>()
        val preferredStationList = ArrayList<Station>()
        val stationList = ArrayList<Station>()

        val userPreferredPetrolType = user.preferredPetrolType?.petrolID
        val userPreferredBrands: ArrayList<Brand> = user.preferredBrands

        stations.forEach { station ->
            if (station.stationPetrolTypeIDs.contains(userPreferredPetrolType)) {
                stationsWithPreferredPetrolType.add(station)
            }
        }

        stationsWithPreferredPetrolType.forEach { station ->
            if (userPreferredBrands.any { brand ->
                        brand.brandID == station.stationBrand
                    }) {
                preferredStationList.add(station)
            }
        }

        when (currentMode) {
            PREFERRED -> {
                stationList.addAll(preferredStationList)

                this.hasPreferredStations = preferredStationList.isNotEmpty()
            }
            NON_PREFERRED -> {
                stationsWithPreferredPetrolType.removeAll(preferredStationList)
                stationList.addAll(stationsWithPreferredPetrolType)

                this.hasNonPreferredStations = stationsWithPreferredPetrolType.isNotEmpty()
            }
        }

        return stationList
    }

    override fun onStop() {
        super.onStop()

        if (this.swipeRefreshLayout.isRefreshing) {
            this.fusedLocationClient.removeLocationUpdates(this.locationCallBack)
            this.swipeRefreshLayout.isRefreshing = false
        }
    }
}