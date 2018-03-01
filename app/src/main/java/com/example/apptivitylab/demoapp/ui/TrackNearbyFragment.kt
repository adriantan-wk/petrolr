package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.NearestStationsAdapter
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.activity_track_nearby.*
import kotlinx.android.synthetic.main.fragment_track_nearby.*
import kotlinx.android.synthetic.main.infowindow_station_details.view.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearbyFragment : Fragment(), GoogleMap.InfoWindowAdapter,
        NearestStationsAdapter.StationViewHolder.onSelectNearestStationListener,
        NearestStationsAdapter.SeeMoreViewHolder.onSelectSeeMoreListener,
        RestAPIClient.OnFullDataReceivedListener, TrackNearActivity.SearchLocationListener {

    companion object {
        const val NO_OF_RESOURCE_SETS = 3

        const val ACCESS_FINE_LOCATION_PERMISSIONS = 100
        const val USER_EXTRA = "user_object"
        const val FROM_LOGIN_EXTRA = "from_login"

        fun newInstance(currentUser: User, isFromLogin: Boolean): TrackNearbyFragment {
            val fragment = TrackNearbyFragment()

            val args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)
            args.putBoolean(FROM_LOGIN_EXTRA, isFromLogin)

            fragment.arguments = args
            return fragment
        }
    }

    private var dataResourcesReceived = 0
    private var performFirstTimeMarkerGeneration = false
    private val malaysiaLatLng = LatLng(3.519863, 101.538116)

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var currentUser: User
    private var userLatLng: LatLng? = null
    private var locationMarker: Marker? = null

    private var brandList: ArrayList<Brand> = ArrayList()
    private var stationList: ArrayList<Station> = ArrayList()
    private var filteredStationList: ArrayList<Station> = ArrayList()
    private var preferredStationList: ArrayList<Station> = ArrayList()
    private var mapOfStationMarkers: HashMap<String, Marker> = HashMap()

    private var nearestStations: ArrayList<Station> = ArrayList()
    private lateinit var nearestStationsAdapter: NearestStationsAdapter
    private var isAdapterInitialized = false

    private lateinit var clusterManager: ClusterManager<Station>
    private lateinit var clusterRenderer: StationMarkerRenderer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (savedInstanceState == null) {
            this.setupGoogleMapFragment()
        } else {
            this.mapFragment = this.activity!!.supportFragmentManager.findFragmentById(R.id.mapContainerFrameLayout) as SupportMapFragment
        }

        this.context?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION_PERMISSIONS)
            }
        }

        return inflater.inflate(R.layout.fragment_track_nearby, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        this.setupGoogleMapFragment()

        when (requestCode) {
            ACCESS_FINE_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val toast = Toast.makeText(context, R.string.location_permissions_granted, Toast.LENGTH_SHORT)
                    toast.show()
                    this.setupClusterManager(this.filteredStationList)

                } else {
                    val toast = Toast.makeText(context, R.string.location_permissions_denied, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fromLogin = false

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
            fromLogin = it.getBoolean(FROM_LOGIN_EXTRA)
        }

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        if (fromLogin) {
            this.loadAppData()
        } else {
            this.performFragmentStartup()
        }
    }

    private fun loadAppData() {
        this.loadStationsProgressBar.visibility = View.VISIBLE
        this.loadStationsProgressBarTextView.visibility = View.VISIBLE

        if (BrandController.brandList.isEmpty()) {
            BrandController.loadBrands(this.context!!, this)
        } else {
            this.dataResourcesReceived++
        }

        if (PetrolTypeController.petrolTypeList.isEmpty()) {
            PetrolTypeController.loadPetrolTypes(this.context!!, this)
        } else {
            this.dataResourcesReceived++
        }

        StationController.loadStations(this.context!!, this)
    }

    override fun onFullDataReceived(dataReceived: Boolean, error: VolleyError?) {
        if (!dataReceived || error != null) {
            view?.let {
                this.dataResourcesReceived = 0
                this.loadStationsProgressBar.visibility = View.GONE
                this.loadStationsProgressBarTextView.visibility = View.GONE

                Snackbar.make(it, getString(R.string.failed_retrieve_data), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.retry), View.OnClickListener {
                            this.loadAppData()
                        })
                        .show()
            }
        } else {
            this.dataResourcesReceived++

            if (dataResourcesReceived == NO_OF_RESOURCE_SETS) {
                if (this.currentUser.preferredPetrolType?.petrolName == null) {
                    this.currentUser.assignUserPreferences()
                }

                this.performFirstTimeMarkerGeneration = true
                this.performFragmentStartup()
            }
        }
    }

    private fun performFragmentStartup() {
        this.stationList = StationController.stationList
        this.brandList = BrandController.brandList

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        this.nearestStationsRecyclerView.layoutManager = layoutManager

        this.loadStationsProgressBar.visibility = View.GONE
        this.loadStationsProgressBarTextView.visibility = View.GONE
        this.refreshNearestStationsButton.visibility = View.VISIBLE
        this.recenterCameraButton.visibility = View.VISIBLE

        this.filteredStationList = this.filterStationsByPreferredPetrol(this.stationList, this.currentUser)
        this.updatePreferredStationList()

        if (this.performFirstTimeMarkerGeneration) {
            this.setupClusterManager(this.filteredStationList)
            this.performFirstTimeMarkerGeneration = false
        }

        this.context?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                this.updateBaseLocation(true, null)
            }
        }

        this.recenterCameraButton.setOnClickListener {
            this.recenterMapCamera()
        }

        this.refreshNearestStationsButton.setOnClickListener {
            if (this.refreshNearestStationsButton.text == getString(R.string.return_to_user_location)) {
                this.refreshNearestStationsButton.text = getString(R.string.refresh_nearest_stations)

                this.activity?.let {
                    it.locationSearchTextView.text = ""
                }
            }

            this.updatePreferredStationList()

            this.refreshProgressBar.visibility = View.VISIBLE
            this.refreshProgressBar.progress = 0
            this.refreshNearestStationsButton.visibility = View.GONE
            this.updateBaseLocation(true, null)
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

    private fun updatePreferredStationList() {
        this.preferredStationList.clear()

        this.preferredStationList.addAll(this.filteredStationList.filter { station ->
            this.currentUser.preferredBrands
                    .map { it.brandID }
                    .contains(station.stationBrand)
        })
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

                this.clusterManager = ClusterManager(this.context, googleMap)
                this.clusterRenderer = StationMarkerRenderer(this.context, googleMap, this.clusterManager)
                this.setupClusterManager(this.filteredStationList)

                if (this.userLatLng == null) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(malaysiaLatLng, 5f)
                    googleMap?.moveCamera(cameraUpdate)
                } else {
                    this.recenterMapCamera()
                }

                with(googleMap) {
                    uiSettings?.isCompassEnabled = false
                    uiSettings?.isZoomControlsEnabled = true
                    uiSettings?.isMapToolbarEnabled = false
                    uiSettings?.isMyLocationButtonEnabled = false

                    context?.let {
                        if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            isMyLocationEnabled = true
                        }
                    }

                    setOnCameraIdleListener(this@TrackNearbyFragment.clusterManager)
                    setOnMarkerClickListener(this@TrackNearbyFragment.clusterManager)
                }

                this.assignInfoWindowAdapterAndListener(this)
            }
        }
    }

    private fun setupClusterManager(stationList: ArrayList<Station>) {
        this.googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysiaLatLng, 5f))

        with(this.clusterManager) {
            clearItems()
            addItems(stationList)
            renderer = this@TrackNearbyFragment.clusterRenderer
            cluster()

            setOnClusterClickListener { cluster ->
                val boundsBuilder = LatLngBounds.Builder()
                cluster.items.forEach { item ->
                    boundsBuilder.include(item.position)
                }
                this@TrackNearbyFragment.googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150))
                true
            }
        }
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return if (marker?.tag is Station) {
            val view: View = activity!!.layoutInflater.inflate(R.layout.infowindow_station_details, null)
            val station: Station = marker.tag as Station

            view.stationNameTextView.text = station.stationName

            view
        } else {
            null
        }
    }

    private fun assignInfoWindowAdapterAndListener(adapter: GoogleMap.InfoWindowAdapter) {
        this.googleMap?.let {
            it.setInfoWindowAdapter(adapter)
            it.setOnInfoWindowClickListener { marker ->
                if (marker?.tag is Station) {
                    val stationDetailsIntent = StationDetailsActivity.newLaunchIntent(context!!, marker.tag as Station, BrandController.brandList)
                    startActivity(stationDetailsIntent)
                }
            }
        }
    }

    override fun onLocationSelected(place: Place) {
        this.activity?.let {
            it.locationSearchTextView.text = place.name
        }

        this.refreshNearestStationsButton.text = getString(R.string.return_to_user_location)

        if (this.locationMarker == null) {
            val markerOptions = MarkerOptions()
                    .position(place.latLng)
                    .title(place.name.toString())
                    .zIndex(5f)

            this.locationMarker = this.googleMap?.addMarker(markerOptions)
        } else {
            this.locationMarker?.let {
                it.position = place.latLng
                it.title = place.name.toString()

                if (it.isInfoWindowShown) {
                    it.hideInfoWindow()
                }
            }
        }

        this.locationMarker?.tag = place.id
        this.updatePreferredStationList()
        this.updateBaseLocation(false, place.latLng)
    }

    @SuppressLint("MissingPermission")
    private fun updateBaseLocation(useTrueLocation: Boolean, locationLatLng: LatLng?) {
        if (useTrueLocation) {
            this.fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.userLatLng = LatLng(location.latitude, location.longitude)

                    if (this.locationMarker != null) {
                        this.locationMarker?.remove()
                        this.locationMarker = null
                    }

                    this.performLocationChangedUIUpdates()
                } else {
                    Toast.makeText(context, getString(R.string.location_update_failed), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            this.userLatLng = locationLatLng

            this.performLocationChangedUIUpdates()
        }
    }

    private fun performLocationChangedUIUpdates() {
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

        this.refreshNearestStationsButton.visibility = View.VISIBLE
        this.refreshProgressBar.visibility = View.GONE
    }

    private fun initializeNearestStationsAdapter() {
        this.nearestStationsAdapter = NearestStationsAdapter()
        this.nearestStationsAdapter.setNearestStationListener(this)
        this.nearestStationsAdapter.setSeeMoreListener(this)

        this.nearestStationsRecyclerView.adapter = this.nearestStationsAdapter

        this.isAdapterInitialized = true
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

    private fun sortStationListByDistance(stationList: ArrayList<Station>): ArrayList<Station> {
        stationList.forEach { station ->
            station.distanceFromUser = this.calculateUserDistanceToStation(station)
        }

        val distanceSortedStationList = ArrayList<Station>()
        distanceSortedStationList.addAll(stationList)
        distanceSortedStationList.sortBy { it.distanceFromUser }

        return distanceSortedStationList
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
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200))
        }
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
        this.updatePreferredStationList()
        this.mapOfStationMarkers.clear()
        this.setupClusterManager(this.filteredStationList)

        this.activity?.let {
            if (it.locationSearchTextView.text.isNotEmpty()) {
                this.updateBaseLocation(false, this.userLatLng)
            } else {
                this.updateBaseLocation(true, null)
            }
        }

        Toast.makeText(context!!, getString(R.string.preferences_updated), Toast.LENGTH_SHORT).show()
    }

    inner class StationMarkerRenderer(context: Context?, googleMap: GoogleMap?, clusterManager: ClusterManager<Station>?)
        : DefaultClusterRenderer<Station>(context, googleMap, clusterManager) {
        override fun onBeforeClusterItemRendered(item: Station, markerOptions: MarkerOptions) {
            val itemBrandID = item.stationBrand
            val bitmapImg: Bitmap
            val resizedBitmapImg: Bitmap

            if (this@TrackNearbyFragment.currentUser.preferredBrands.any { it.brandID == itemBrandID }) {
                bitmapImg = BitmapFactory.decodeResource(resources, R.drawable.ic_gasstation_marker)
                resizedBitmapImg = Bitmap.createScaledBitmap(bitmapImg, 100, 100, false)
            } else {
                bitmapImg = BitmapFactory.decodeResource(resources, R.drawable.ic_dot_marker)
                resizedBitmapImg = Bitmap.createScaledBitmap(bitmapImg, 50, 50, false)
            }

            markerOptions
                    .title(item.title)
                    .position(item.position)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmapImg))

            super.onBeforeClusterItemRendered(item, markerOptions)
        }

        override fun onClusterItemRendered(clusterItem: Station?, marker: Marker?) {
            clusterItem?.stationID?.let {
                marker?.let { marker ->
                    marker.tag = clusterItem
                    this@TrackNearbyFragment.mapOfStationMarkers.put(it, marker)
                }
            }

            super.onClusterItemRendered(clusterItem, marker)
        }
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
}
