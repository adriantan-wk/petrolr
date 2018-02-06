package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.BrandController
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
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearbyFragment : Fragment(), GoogleMap.InfoWindowAdapter {

    companion object {
        val ACCESS_FINE_LOCATION_PERMISSIONS = 100
        const val USER_EXTRA = "user_object"
        const val STATION_LIST_EXTRA = "station_list"
        const val BRAND_LIST_EXTRA = "brand_list"

        fun newInstance(currentUser: User, stations: ArrayList<Station>, brands: ArrayList<Brand>): TrackNearbyFragment {
            val fragment = TrackNearbyFragment()

            val args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)
            args.putParcelableArrayList(STATION_LIST_EXTRA, stations)
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
    private var mapCircle: Circle? = null

    private var brandList: ArrayList<Brand> = ArrayList()
    private var stationList: ArrayList<Station> = ArrayList()
    private var filteredStationList: ArrayList<Station> = ArrayList()
    private var preferredStationList: ArrayList<Station> = ArrayList()
    private var mapOfStationMarkers: HashMap<String, Marker> = HashMap()
    private var nearestStation: Station? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (savedInstanceState == null) {
            setupGoogleMapFragment()
        } else {
            mapFragment = activity!!.supportFragmentManager.
                    findFragmentById(R.id.mapContainerFrameLayout) as SupportMapFragment
        }

        return inflater.inflate(R.layout.fragment_track_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
            this.stationList = it.getParcelableArrayList(STATION_LIST_EXTRA)
            this.brandList = it.getParcelableArrayList(BRAND_LIST_EXTRA)
        }

        nearestStationLinearLayout.setOnClickListener {
            this.nearestStation?.let {
                val nearestStationMarker = this.mapOfStationMarkers[it.stationID]
                nearestStationMarker?.showInfoWindow()
                this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.stationLatLng, 15f))
            }
        }

        showNearestStationsButton.setOnClickListener {
            this.updateUserLocation()
        }

        this.currentUser.preferredPetrolType?.let {
            this.priceTextView.text = getString(R.string.petrol_price, it.petrolName)
            this.priceValueTextView.text = getString(R.string.price_value, it.currentPrice)
        }

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
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
        mapFragment = SupportMapFragment.newInstance()

        activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainerFrameLayout, mapFragment)
                .commit()

        mapFragment?.let {
            it.getMapAsync { googleMap ->
                this.googleMap = googleMap

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
                    isMyLocationEnabled = true
                }

                this.assignInfoWindowAdapterAndListener(this)
                this.filteredStationList = filterStationsByPreferredPetrol(this.stationList, this.currentUser)
                this.generateStationMarkers(filteredStationList)
            }
        }
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View? {
        val view: View = activity!!.layoutInflater.inflate(R.layout.infowindow_station_details, null)

        val station: Station = marker?.tag as Station

        view.stationNameTextView.text = station.stationName
        view.stationAddressTextView.text = station.stationAddress

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

    @SuppressLint("MissingPermission")
    private fun updateUserLocation() {
        this.fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                this.userLatLng = LatLng(location.latitude, location.longitude)

                nearestStation = this.findNearestStation()
                this.updateNearestStationViews(this.nearestStation)

                this.recenterMapCamera()
            }
        }
    }

    private fun findNearestStation(): Station? {
        var nearestStation: Station? = this.nearestStation

        if (this.preferredStationList.isNotEmpty()) {
            val distanceArrangedStationList = sortStationListByDistance(preferredStationList)
            nearestStation = distanceArrangedStationList[0]
            this.updateNearestStationViews(nearestStation)
        } else {
            nearestStation = null
        }
        return nearestStation
    }

    private fun updateNearestStationViews(nearestStation: Station?) {
        if (nearestStation != null) {
            if (isAdded) {
                this.brandList.forEach { brand ->
                    if (brand.brandName == nearestStation.stationBrand) {
                        this.stationImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, brand.brandLogo, null))
                    }
                }

                this.nameTextView.text = nearestStation.stationName
                this.addressTextView.text = nearestStation.stationAddress
                this.distanceTextView.text = "%.2f".format(nearestStation.distanceFromUser) +
                        " " + getString(R.string.distance_km_away)
            }
        } else {
            this.nameTextView.text = getString(R.string.searching)
            this.addressTextView.text = ""
            this.distanceTextView.text = ""
        }
    }

    private fun recenterMapCamera() {
        this.googleMap?.let { googleMap ->
            this.userLatLng?.let { userLatLng ->
                val stationList = sortStationListByDistance(this.preferredStationList)
                val nearestStations = ArrayList<Station>()

                if (stationList.size < 5) {
                    nearestStations.addAll(stationList)
                } else {
                    nearestStations.addAll(stationList.take(5))
                }

                val bounds = LatLngBounds.Builder()

                nearestStations.forEach { station ->
                    station.stationLatLng?.let {
                        val stationLatLng = LatLng(it.latitude, it.longitude)
                        bounds.include(stationLatLng)
                    }
                }

                bounds.include(this.userLatLng)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
            }
        }
    }

    private fun sortStationListByDistance(preferredStationList: ArrayList<Station>): ArrayList<Station> {
        preferredStationList.forEach { station ->
            station.distanceFromUser = this.calculateUserDistanceToStation(station)
        }

        val distanceSortedStationList = ArrayList<Station>()
        distanceSortedStationList.addAll(preferredStationList)

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

    public fun onUserPreferencesChanged(user: User) {

        this.currentUser = user
        this.filteredStationList = this.filterStationsByPreferredPetrol(this.stationList, this.currentUser)

        this.currentUser.preferredPetrolType?.let {
            priceTextView.text = getString(R.string.petrol_price, it.petrolName)
            priceValueTextView.text = getString(R.string.price_value, it.currentPrice)
        }

        this.mapOfStationMarkers.values.forEach { marker ->
            marker.remove()
        }
        this.mapOfStationMarkers.clear()
        this.generateStationMarkers(this.filteredStationList)

        this.nearestStation = null

        Toast.makeText(context!!, getString(R.string.preferences_updated), Toast.LENGTH_LONG).show()
        this.updateUserLocation()
    }

    private fun filterStationsByPreferredPetrol(stationList: ArrayList<Station>, currentUser: User): ArrayList<Station> {
        val stationsWithCorrectPetrolType = ArrayList<Station>()
        val preferredPetrolType: PetrolType? = currentUser.preferredPetrolType

        stationList.forEach { station ->
            if (station.stationPetrolTypeIDs.contains(preferredPetrolType?.petrolID)) {
                stationsWithCorrectPetrolType.add(station)
            }
        }

        return stationsWithCorrectPetrolType
    }

    private fun generateStationMarkers(filteredStationList: ArrayList<Station>) {
        preferredStationList.clear()

        for (station in filteredStationList) {
            station.stationLatLng?.apply {
                val stationLatLng = LatLng(latitude, longitude)

                val bitmapImg: Bitmap
                val resizedBitmapImg: Bitmap

                if (currentUser.preferredBrands.any { brand ->
                    brand.brandName == station.stationBrand
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
}
