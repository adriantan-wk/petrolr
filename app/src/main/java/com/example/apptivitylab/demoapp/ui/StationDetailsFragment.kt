package com.example.apptivitylab.demoapp.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.fragment_station_details.*

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class StationDetailsFragment : Fragment() {
    companion object {
        const val STATION_DETAILS = "station_details"
        const val BRAND_LIST_EXTRA = "brand_list"

        fun newInstance(station: Station, brands: ArrayList<Brand>): StationDetailsFragment {
            val fragment = StationDetailsFragment()

            val args: Bundle = Bundle()
            args.putParcelable(STATION_DETAILS, station)
            args.putParcelableArrayList(BRAND_LIST_EXTRA, brands)

            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var station: Station
    private lateinit var brands: ArrayList<Brand>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_station_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.station = it.getParcelable(STATION_DETAILS)
            this.brands = it.getParcelableArrayList(BRAND_LIST_EXTRA)
        }

        goButton.setOnClickListener {
            this.navigateToStation(this.station)
        }

        this.updateView()
    }

    private fun navigateToStation(station: Station) {
        station.stationLatLng?.let {

            val locationUri = Uri.parse("geo:0,0?q=${it.latitude},${it.longitude}")

            val navigateIntent = Intent(Intent.ACTION_VIEW, locationUri)

            val packageManager = context!!.packageManager
            val availableApps = packageManager.queryIntentActivities(navigateIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            val isIntentSafe = availableApps.size > 0

            val chooser = Intent.createChooser(navigateIntent, getString(R.string.navigate))

            if (isIntentSafe) {
                startActivity(chooser)
            } else {
                Toast.makeText(context!!, getString(R.string.no_navigation_apps), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateView() {
        this.brands.forEach { brand ->
            if (brand.brandName == this.station.stationBrand) {
                this.stationImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, brand.brandLogo, null))
            }
        }

        this.nameTextView.text = this.station.stationName
        this.idTextView.text = this.station.stationID
        this.brandTextView.text = this.station.stationBrand
        this.addressTextView.text = this.station.stationAddress

        this.petrolTypesTextView.text = PetrolTypeController.petrolTypeList
                .filter { petrol -> this.station.stationPetrolTypeIDs.contains(petrol.petrolID) }
                .map { it.petrolName }
                .joinToString(getString(R.string.list_separator))
    }
}