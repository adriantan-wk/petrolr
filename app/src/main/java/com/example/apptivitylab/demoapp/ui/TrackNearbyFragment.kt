package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.MockDataLoader
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_track_nearby.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearbyFragment : Fragment() {

    companion object {
        val ACCESS_FINE_LOCATION_PERMISSIONS = 100
    }

    private var mapFragment : SupportMapFragment? = null
    private var googleMap : GoogleMap? = null

    private var fusedLocationClient : FusedLocationProviderClient? = null
    private var locationCallBack : LocationCallback? = null

    private var userLocationMarker: Marker? = null
    private var stationMarkersExist : Boolean = false
    private var userLatLng : LatLng? = null

    private var listOfStations : ArrayList<Station> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

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

        context?.let {
            listOfStations = MockDataLoader.loadStations(it)
        }

        recenterFAB.setOnClickListener {
            if (userLatLng == null) {
                Toast.makeText(context, R.string.feature_unavailable_string, Toast.LENGTH_SHORT).show()
            } else {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)

                this.googleMap?.let {
                    it.moveCamera(cameraUpdate)
                }

                Toast.makeText(context, R.string.recenter_msg_text, Toast.LENGTH_SHORT).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        startLocationUpdates()
    }

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

            this.googleMap?.let {
                it.moveCamera(cameraUpdate)
                }
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
                    val toast = Toast.makeText(context,R.string.location_permissions_granted_string, Toast.LENGTH_SHORT)
                    toast.show()
                    startLocationUpdates()
                } else {
                    val toast = Toast.makeText(context, R.string.location_permissions_denied_string, Toast.LENGTH_SHORT)
                    toast.show()
                }
            } else -> { }
        }
    }

    private fun onLocationChanged(location: Location?) {

        location?.let {
            //The use of these textviews to display coordinates is temporary
            //TODO Change to distance, time and price when information is available
            if (isAdded) {
                distanceTextView.text = getString(R.string.latitude_string) + ": " + it.latitude.toString()
                timeTextView.text = getString(R.string.longitude_string) + ": " + it.longitude.toString()
                priceTextView.text = getString(R.string.accuracy_string) + ": " + it.accuracy.toString()
            }

            userLatLng = LatLng(it.latitude, it.longitude)

                if (userLocationMarker == null) {
                    userLatLng?.let {
                        val markerOptions = MarkerOptions().position(it).title(getString(R.string.user_marker_string))

                        googleMap?.let {
                            userLocationMarker = it.addMarker(markerOptions)
                        }
                    }
                } else {
                    userLocationMarker?.let {
                        it.position = userLatLng
                    }
                }
            }

            if (!stationMarkersExist) {
                Toast.makeText(context!!, getString(R.string.generating_markers_string), Toast.LENGTH_SHORT).show()
                generateStationMarkers()
            }
        }


    private fun generateStationMarkers() {
        for (station in listOfStations) {
            station.stationLatLng?.apply {
                val stationLatLng = LatLng(latitude, longitude)
                val bitmapImg: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.gasstation_marker)
                val resizedBitmapImg = Bitmap.createScaledBitmap(bitmapImg, 100, 100, false)

                val stationMarkerOptions: MarkerOptions = MarkerOptions().position(stationLatLng)
                        .title(station.stationName).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmapImg))

                googleMap?.let {
                    it.addMarker(stationMarkerOptions)
                    stationMarkersExist = true
                }
            }

        }
    }

    override fun onStop() {
        fusedLocationClient?.removeLocationUpdates(locationCallBack)
        super.onStop()
    }

}
