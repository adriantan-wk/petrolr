package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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

    private lateinit var station: Station

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_details)

        setSupportActionBar(stationDetailsToolbar)

        stationDetailsToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        stationDetailsToolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        this.station = intent.getParcelableExtra<Station>(STATION_EXTRA)
        supportActionBar?.title = this.station.stationName

        val brands = intent.getParcelableArrayListExtra<Brand>(BRAND_LIST_EXTRA)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.stationDetailsContainerFrameLayout, StationDetailsFragment.newInstance(this.station, brands))
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.station_details_go_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == R.id.go) {
            this.navigateToStation(this.station)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigateToStation(station: Station) {
        station.stationLatLng?.let {

            val locationUri = Uri.parse("geo:0,0?q=${it.latitude},${it.longitude}")

            val navigateIntent = Intent(Intent.ACTION_VIEW, locationUri)

            val packageManager = this.packageManager
            val availableApps = packageManager.queryIntentActivities(navigateIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            val isIntentSafe = availableApps.size > 0

            val chooser = Intent.createChooser(navigateIntent, getString(R.string.navigate))

            if (isIntentSafe) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, getString(R.string.no_navigation_apps), Toast.LENGTH_LONG).show()
            }
        }
    }
}