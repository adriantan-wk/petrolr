package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.apptivitylab.demoapp.R
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_track_nearby.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_nearby)

        setSupportActionBar(trackNearToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerToggle = ActionBarDrawerToggle(this, trackNearDrawerLayout,
                trackNearToolbar, R.string.open_drawer_string, R.string.close_drawer_string)
        trackNearDrawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        trackNearNavView.inflateMenu(R.menu.navigation_drawer_home_menu)
        trackNearNavView.setNavigationItemSelectedListener {
            when (it.itemId) {
                //TODO Other side drawer menu items

                R.id.nav_track_nearby -> {
                    trackNearDrawerLayout.closeDrawers()
                    true
                }

                R.id.nav_station_list -> {
                    val stationListIntent = Intent(this, StationListActivity::class.java)
                    startActivity(stationListIntent)

                    trackNearDrawerLayout.closeDrawers()
                    true
                }

                R.id.nav_logout -> {
                    AlertDialog.Builder(this)
                            .setIcon(R.drawable.logout)
                            .setTitle("Log Out")
                            .setMessage("Are you sure you want to log out?")
                            .setPositiveButton("Yes",
                                    { dialog, which ->
                                        val logOutIntent = Intent(this, TitleActivity::class.java)
                                        startActivity(logOutIntent)
                                    })
                            .setNegativeButton("No", null)
                            .show()

                    true
                }

                else -> {
                    val toast = Toast.makeText(this, "Sorry, this isn't available yet", Toast.LENGTH_SHORT)
                    toast.show()

                    false
                }
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrameLayout, TrackNearbyFragment())
                .commit()
    }

}