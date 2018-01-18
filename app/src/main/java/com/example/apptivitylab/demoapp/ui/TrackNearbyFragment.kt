package com.example.apptivitylab.demoapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.R

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearbyFragment : Fragment() {

    companion object {
        val ACCESS_FINE_LOCATION_PERMISSIONS = 101
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_track_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_FINE_LOCATION_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_FINE_LOCATION_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val toast = Toast.makeText(context,"Location Permissions Granted", Toast.LENGTH_SHORT)
                    toast.show()
                    //TODO Allow Map Stuff
                } else {
                    val toast = Toast.makeText(context,"Location Permissions Denied", Toast.LENGTH_SHORT)
                    toast.show()
                    //TODO If permission is denied
                }
            } else -> {
                //No need to do anything here
            }
        }
    }
}