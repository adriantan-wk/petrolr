package com.example.apptivitylab.demoapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.FrameLayout
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.Station
import kotlinx.android.synthetic.main.activity_station_details.*

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class StationDetailsActivity : AppCompatActivity() {
    private lateinit var toolbar : Toolbar
    private lateinit var container : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_details)

        toolbar = stationDetailsToolbar
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        })

        val item = intent.getParcelableExtra<Station>("Selected Station")
        supportActionBar?.title = item.stationName

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.stationDetailsContainerFrameLayout, StationDetailsFragment.newInstance(item))
                .commit()
    }
}