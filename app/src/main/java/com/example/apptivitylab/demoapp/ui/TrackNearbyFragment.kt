package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import com.example.apptivitylab.demoapp.ui.TrackNearbyFragment.Companion.ACCESS_FINE_LOCATION_PERMISSIONS
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_track_nearby.*
import kotlinx.android.synthetic.main.infowindow_station_details.view.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearbyFragment : Fragment(), GoogleMap.InfoWindowAdapter {

    companion object {
        val ACCESS_FINE_LOCATION_PERMISSIONS = 100
        const val USER_EXTRA = "user_object"
        const val STATION_LIST_EXTRA = "station_list"

        fun newInstance(currentUser: User, stations: ArrayList<Station>): TrackNearbyFragment {
            val fragment = TrackNearbyFragment()

            val args = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)
            args.putParcelableArrayList(STATION_LIST_EXTRA, stations)

            fragment.arguments = args
            return fragment
        }
    }

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallBack: LocationCallback? = null

    private lateinit var currentUser: User
    private var userLatLng: LatLng? = null
    private var performInitialUserLocationZoom = true

    private var stationList: ArrayList<Station> = ArrayList()
    private var filteredStationList: ArrayList<Station> = ArrayList()
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
        }

        nearestStationLinearLayout.setOnClickListener {
            this.nearestStation?.let {
                val nearestStationMarker = this.mapOfStationMarkers[it.stationID]
                nearestStationMarker?.showInfoWindow()
                this.googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it.stationLatLng, 15f))
            }
        }

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
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

                val startLatLng = LatLng(4.2105, 101.9758)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 6.0f)

                with(googleMap){
                    moveCamera(cameraUpdate)
                    uiSettings?.isCompassEnabled = false
                    uiSettings?.isZoomControlsEnabled = true
                    isMyLocationEnabled = true
                }

                assignInfoWindowAdapterAndListener(this)
                this.filteredStationList = filterStationsByPreferredPetrol(this.stationList, this.currentUser)
                generateStationMarkers(filteredStationList)
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
                val stationDetailsIntent = StationDetailsActivity.newLaunchIntent(context!!, marker.tag as Station)
                startActivity(stationDetailsIntent)
            }
        }
    }

    private fun startLocationUpdates() {

        this.context?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION_PERMISSIONS)
            }
        }

        var request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.interval = 5000
        request.fastestInterval = 3000

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
            ACCESS_FINE_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val toast = Toast.makeText(context, R.string.location_permissions_granted_string, Toast.LENGTH_SHORT)
                    toast.show()
                    startLocationUpdates()
                } else {
                    val toast = Toast.makeText(context, R.string.location_permissions_denied_string, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    private fun onLocationChanged(location: Location?) {

        location?.let {
            userLatLng = LatLng(it.latitude, it.longitude)

            if (this.performInitialUserLocationZoom) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                googleMap?.animateCamera(cameraUpdate)
                this.performInitialUserLocationZoom = false
            }
        }

        nearestStation = this.findNearestStation()
    }

    public fun onUserPreferencesChanged(user: User) {
        fusedLocationClient?.removeLocationUpdates(locationCallBack)

        this.currentUser = user
        this.filteredStationList = this.filterStationsByPreferredPetrol(this.stationList, this.currentUser)

        this.mapOfStationMarkers.values.forEach { marker ->
            marker.remove()
        }
        this.mapOfStationMarkers.clear()
        this.generateStationMarkers(this.filteredStationList)

        this.nearestStation = null

        Toast.makeText(context!!, getString(R.string.preferences_updated_string), Toast.LENGTH_LONG).show()
        startLocationUpdates()
    }

    private fun findNearestStation(): Station? {
        var nearestStation: Station? = this.nearestStation

        if (this.filteredStationList.isNotEmpty()) {
            if (nearestStation == null) {
                this.filteredStationList[0].distanceFromUser = calculateUserDistanceToStation(this.filteredStationList[0])

                updateNearestStationViews(nearestStation)

                nearestStation = this.filteredStationList[0]
            } else {
                this.filteredStationList.forEach { station ->
                    val distanceFromUser = calculateUserDistanceToStation(station)
                    station.distanceFromUser = distanceFromUser

                    nearestStation?.distanceFromUser?.let {
                        if (distanceFromUser < it) {
                            nearestStation = station
                        }
                    }
                }
                updateNearestStationViews(nearestStation)
            }
        } else {
            nearestStation = null
        }
        return nearestStation
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
        for (station in filteredStationList) {
            station.stationLatLng?.apply {
                val stationLatLng = LatLng(latitude, longitude)
                val bitmapImg: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.gasstation_marker)
                val resizedBitmapImg = Bitmap.createScaledBitmap(bitmapImg, 100, 100, false)

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

    private fun updateNearestStationViews(nearestStation: Station?) {
        if (nearestStation != null) {
            if (isAdded) {
                nameTextView.text = nearestStation.stationName
                addressTextView.text = nearestStation.stationAddress
                distanceTextView.text = "%.2f".format(nearestStation.distanceFromUser) +
                        " " + getString(R.string.distance_km_away_string)
            }
        } else {
            nameTextView.text = getString(R.string.searching_string)
            addressTextView.text = ""
            distanceTextView.text = ""
        }
    }

    private fun calculateUserDistanceToStation(station: Station): Float {
        var userLocation = Location(getString((R.string.current_location_string)))
        var stationLocation = Location(getString(R.string.destination_string))

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

    override fun onStop() {
        fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }
}
