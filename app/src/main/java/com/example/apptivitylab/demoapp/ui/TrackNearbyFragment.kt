package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.NearestStationsAdapter
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_track_nearby.*
import kotlinx.android.synthetic.main.infowindow_station_details.view.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearbyFragment : Fragment(), GoogleMap.InfoWindowAdapter,
        NearestStationsAdapter.StationViewHolder.onSelectNearestStationListener,
        NearestStationsAdapter.SeeMoreViewHolder.onSelectSeeMoreListener {

    companion object {
        const val MAX_NO_OF_STATIONS_DISPLAYED = 35

        const val ACCESS_FINE_LOCATION_PERMISSIONS = 100
        const val USER_EXTRA = "user_object"
        const val BRAND_LIST_EXTRA = "brand_list"

        fun newInstance(currentUser: User, brands: ArrayList<Brand>): TrackNearbyFragment {
            val fragment = TrackNearbyFragment()

            val args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)
            args.putParcelableArrayList(BRAND_LIST_EXTRA, brands)

            fragment.arguments = args
            return fragment
        }
    }

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var performInitialMapZoom = true

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var currentUser: User
    private var userLatLng: LatLng? = null

    private var brandList: ArrayList<Brand> = ArrayList()
    private var stationList: ArrayList<Station> = ArrayList()
    private var filteredStationList: ArrayList<Station> = ArrayList()
    private var displayedStationList: ArrayList<Station> = ArrayList()
    private var preferredStationList: ArrayList<Station> = ArrayList()
    private var mapOfStationMarkers: HashMap<String, Marker> = HashMap()

    private var nearestStations: ArrayList<Station> = ArrayList()
    private lateinit var nearestStationsAdapter: NearestStationsAdapter
    private var isAdapterInitialized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (savedInstanceState == null) {
            this.setupGoogleMapFragment()
        } else {
            this.mapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.mapContainerFrameLayout) as SupportMapFragment
        }

        return inflater.inflate(R.layout.fragment_track_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
            this.brandList = it.getParcelableArrayList(BRAND_LIST_EXTRA)
        }

        this.stationList = StationController.stationList

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        this.nearestStationsRecyclerView.layoutManager = layoutManager

        this.recenterCameraButton.setOnClickListener {
            this.recenterMapCamera()
        }

        this.refreshNearestStationsButton.setOnClickListener {
            this.clearMapMarkers()
            this.updateUserLocation()
            Toast.makeText(context, getString(R.string.user_location_refreshed), Toast.LENGTH_SHORT).show()
        }

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
    }

    override fun onNearestStationSelected(station: Station) {
        val stationMarker = this.mapOfStationMarkers[station.stationID]
        stationMarker?.showInfoWindow()
        this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(station.stationLatLng, 15f))
    }

    override fun onSeeMoreSelected() {
        val stationListIntent = StationListActivity.newLaunchIntent(this.context!!, true)
        startActivity(stationListIntent)
    }

    override fun onStart() {
        super.onStart()

        this.context?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION_PERMISSIONS)
            } else {
                if (this.performInitialMapZoom) {
                    this.performInitialMapZoom = false
                    this.updateUserLocation()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_FINE_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val toast = Toast.makeText(context, R.string.location_permissions_granted, Toast.LENGTH_SHORT)
                    toast.show()

                    if (this.performInitialMapZoom) {
                        this.performInitialMapZoom = false
                        this.updateUserLocation()
                    }
                } else {
                    val toast = Toast.makeText(context, R.string.location_permissions_denied, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupGoogleMapFragment() {
        this.mapFragment = SupportMapFragment.newInstance()

        this.activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainerFrameLayout, mapFragment)
                .commit()

        this.mapFragment?.let {
            it.getMapAsync { googleMap ->
                this.googleMap = googleMap

                googleMap.setPadding(0, 0, 0, 100)

                if (this.userLatLng == null) {
                    val startLatLng = LatLng(4.2105, 101.9758)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 6.0f)
                    googleMap?.moveCamera(cameraUpdate)
                } else {
                    this.recenterMapCamera()
                }

                with(googleMap) {
                    uiSettings?.isCompassEnabled = false
                    uiSettings?.isZoomControlsEnabled = true
                    uiSettings?.isMapToolbarEnabled = false
                    isMyLocationEnabled = true
                }

                this.assignInfoWindowAdapterAndListener(this)
                this.filteredStationList = this.filterStationsByPreferredPetrol(this.stationList, this.currentUser)
            }
        }
    }

    private fun filterStationsByPreferredPetrol(stationList: ArrayList<Station>, currentUser: User): ArrayList<Station> {
        val stationsWithPreferredPetrolType = ArrayList<Station>()
        val preferredPetrolType: PetrolType? = currentUser.preferredPetrolType

        stationList.forEach { station ->
            if (station.stationPetrolTypeIDs.contains(preferredPetrolType?.petrolID)) {
                stationsWithPreferredPetrolType.add(station)
            }
        }

        return stationsWithPreferredPetrolType
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View? {
        val view: View = activity!!.layoutInflater.inflate(R.layout.infowindow_station_details, null)

        val station: Station = marker?.tag as Station

        view.stationNameTextView.text = station.stationName

        return view
    }

    private fun assignInfoWindowAdapterAndListener(adapter: GoogleMap.InfoWindowAdapter) {
        this.googleMap?.let {
            it.setInfoWindowAdapter(adapter)
            it.setOnInfoWindowClickListener { marker ->
                val stationDetailsIntent = StationDetailsActivity.newLaunchIntent(context!!, marker.tag as Station, BrandController.brandList)
                startActivity(stationDetailsIntent)
            }
        }
    }

    private fun initializeNearestStationsAdapter() {
        this.nearestStationsAdapter = NearestStationsAdapter()
        this.nearestStationsAdapter.setNearestStationListener(this)
        this.nearestStationsAdapter.setSeeMoreListener(this)
        this.nearestStationsRecyclerView.adapter = this.nearestStationsAdapter

        this.isAdapterInitialized = true
    }

    @SuppressLint("MissingPermission")
    private fun updateUserLocation() {
        this.fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                this.userLatLng = LatLng(location.latitude, location.longitude)

                this.displayedStationList = this.filterDisplayedStations(this.filteredStationList)
                this.generateStationMarkers(this.displayedStationList)

                this.assignNearestStations(this.nearestStations)

                if (!this.isAdapterInitialized) {
                    if (this.nearestStations.isNotEmpty()) {
                        this.initializeNearestStationsAdapter()
                    }
                }

                if (this.isAdapterInitialized) {
                    this.nearestStationsAdapter.updateDataSet(this.nearestStations, this.brandList)
                }

                this.recenterMapCamera()
            }
        }
    }

    private fun filterDisplayedStations(filteredStationList: ArrayList<Station>): ArrayList<Station> {
        var displayedStationList: ArrayList<Station> = ArrayList()

        val distanceSortedList = this.sortStationListByDistance(filteredStationList)

        if (filteredStationList.size > MAX_NO_OF_STATIONS_DISPLAYED) {
            displayedStationList.addAll(distanceSortedList.subList(0, MAX_NO_OF_STATIONS_DISPLAYED))
        } else {
            displayedStationList.addAll(distanceSortedList)
        }

        return displayedStationList
    }

    private fun assignNearestStations(nearestStations: ArrayList<Station>) {
        nearestStations.clear()

        var distanceArrangedStationList: ArrayList<Station> = if (this.preferredStationList.isNotEmpty()) {
            this.sortStationListByDistance(this.preferredStationList)
        } else {
            this.sortStationListByDistance(this.filteredStationList)
        }

        val noOfStations = distanceArrangedStationList.size

        if (noOfStations >= 5) {
            (0 until 5).forEach { counter ->
                nearestStations.add(distanceArrangedStationList[counter])
            }
        } else {
            (0 until noOfStations).forEach { counter ->
                nearestStations.add(distanceArrangedStationList[counter])
            }
        }
    }

    private fun recenterMapCamera() {
        this.googleMap?.let { googleMap ->
            val bounds = LatLngBounds.Builder()

            this.nearestStations.forEach { station ->
                station.stationLatLng?.let {
                    val stationLatLng = LatLng(it.latitude, it.longitude)
                    bounds.include(stationLatLng)
                }
            }

            bounds.include(this.userLatLng)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
        }
    }

    private fun sortStationListByDistance(stationList: ArrayList<Station>): ArrayList<Station> {
        stationList.forEach { station ->
            station.distanceFromUser = this.calculateUserDistanceToStation(station)
        }

        val distanceSortedStationList = ArrayList<Station>()
        distanceSortedStationList.addAll(stationList)

        distanceSortedStationList.sortBy { it.distanceFromUser }

        return distanceSortedStationList
    }


    private fun calculateUserDistanceToStation(station: Station): Float {
        val userLocation = Location(getString((R.string.current_location)))
        val stationLocation = Location(getString(R.string.destination))

        userLatLng?.let {
            userLocation.latitude = it.latitude
            userLocation.longitude = it.longitude
        }

        station.stationLatLng?.let {
            stationLocation.latitude = it.latitude
            stationLocation.longitude = it.longitude
        }

        return userLocation.distanceTo(stationLocation) / 1000
    }

    fun onUserPreferencesChanged(user: User) {

        this.currentUser = user
        this.filteredStationList = this.filterStationsByPreferredPetrol(this.stationList, this.currentUser)

        this.clearMapMarkers()

        Toast.makeText(context!!, getString(R.string.preferences_updated), Toast.LENGTH_SHORT).show()
        this.updateUserLocation()
    }

    private fun generateStationMarkers(displayedStationList: ArrayList<Station>) {
        preferredStationList.clear()

        for (station in displayedStationList) {
            station.stationLatLng?.apply {
                val stationLatLng = LatLng(latitude, longitude)

                val bitmapImg: Bitmap
                val resizedBitmapImg: Bitmap

                if (currentUser.preferredBrands.any { brand ->
                            brand.brandID == station.stationBrand
                        }) {
                    bitmapImg = BitmapFactory.decodeResource(resources, R.drawable.ic_gasstation_marker)
                    resizedBitmapImg = Bitmap.createScaledBitmap(bitmapImg, 100, 100, false)
                    preferredStationList.add(station)
                } else {
                    bitmapImg = BitmapFactory.decodeResource(resources, R.drawable.ic_dot_marker)
                    resizedBitmapImg = Bitmap.createScaledBitmap(bitmapImg, 50, 50, false)
                }

                val stationMarkerOptions: MarkerOptions = MarkerOptions().position(stationLatLng)
                        .title(station.stationName).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmapImg))

                googleMap?.let {
                    val stationMarker = it.addMarker(stationMarkerOptions)
                    stationMarker.tag = station

                    station.stationID?.let {
                        mapOfStationMarkers.put(it, stationMarker)
                    }
                }
            }
        }
    }

    private fun clearMapMarkers() {
        this.mapOfStationMarkers.values.forEach { marker ->
            marker.remove()
        }
        this.mapOfStationMarkers.clear()
    }
}
