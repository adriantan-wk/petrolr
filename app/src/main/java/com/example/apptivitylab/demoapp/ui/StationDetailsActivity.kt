package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.activity_station_details.*

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class StationDetailsActivity : AppCompatActivity() {

    companion object {
        const val STATION_EXTRA = "station_object"
        const val BRAND_LIST_EXTRA = "brand_list"

        fun newLaunchIntent(context: Context, station: Station, brands: ArrayList<Brand>): Intent {
            val intent = Intent(context, StationDetailsActivity::class.java)
            intent.putExtra(STATION_EXTRA, station)
            intent.putParcelableArrayListExtra(BRAND_LIST_EXTRA, brands)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_details)

        setSupportActionBar(stationDetailsToolbar)

        stationDetailsToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        stationDetailsToolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        val item = intent.getParcelableExtra<Station>(STATION_EXTRA)
        supportActionBar?.title = item.stationName

        val brands = intent.getParcelableArrayListExtra<Brand>(BRAND_LIST_EXTRA)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.stationDetailsContainerFrameLayout, StationDetailsFragment.newInstance(item, brands))
                .commit()
    }
}