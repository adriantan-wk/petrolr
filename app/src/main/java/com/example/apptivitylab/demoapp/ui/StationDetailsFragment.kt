package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


        this.updateView()
    }

    private fun updateView() {
        this.brands.forEach { brand ->
            if (brand.brandID == this.station.stationBrand) {
                this.stationImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, brand.brandLogo, null))
                this.brandTextView.text = brand.brandName
            }
        }

        this.nameTextView.text = this.station.stationName
        this.addressTextView.text = this.station.stationAddress

        this.petrolTypesTextView.text = PetrolTypeController.petrolTypeList
                .filter { petrol -> this.station.stationPetrolTypeIDs.contains(petrol.petrolID) }
                .map { it.petrolName }
                .joinToString(getString(R.string.list_separator))
    }
}