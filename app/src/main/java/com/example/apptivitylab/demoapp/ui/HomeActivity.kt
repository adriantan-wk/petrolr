package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.apptivitylab.demoapp.R
import kotlinx.android.synthetic.main.activity_home.*
import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.Toast

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class HomeActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Set up toolbar, drawerlayout and navigationview
        toolbar = homeActivityToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = homeActivityDrawerLayout
        navView = homeActivityNavView

        //Create 'hamburger button' in toolbar to open DrawerLayout
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer_string, R.string.close_drawer_string)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        //Create navigationview menu
        navView.inflateMenu(R.menu.navigation_drawer_home_menu)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                //TODO Other side drawer menu items

                R.id.nav_track_nearby -> { //Go to the track nearby stations screen
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.homeActivityContainer, TrackNearbyFragment())
                            .addToBackStack("To Track Nearby")
                            .commit()

                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_station_list -> { //Go to the station list screen
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.homeActivityContainer, StationListFragment())
                            .addToBackStack("To Station List")
                            .commit()

                    drawerLayout.closeDrawers()
                    true
                }

                R.id.nav_logout -> { //Log Out
                    AlertDialog.Builder(this)
                            .setIcon(R.drawable.logout) //Create an alert to ask user if they wish to log out
                            .setTitle("Log Out")
                            .setMessage("Are you sure you want to log out?")
                            .setPositiveButton("Yes",
                                    { dialog, which ->
                                        val logOutIntent = Intent(this, TitleActivity::class.java)
                                        startActivity(logOutIntent) //Go to login screen
                                    })
                            .setNegativeButton("No", null) //Do nothing if user picks no
                            .show()

                    true
                }

                else -> { //Unavailable menu buttons
                    val toast = Toast.makeText(this, "Sorry, this isn't available yet", Toast.LENGTH_SHORT)
                    toast.show()

                    false
                }
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.homeActivityContainer, StationListFragment())
                .commit()
    }

}