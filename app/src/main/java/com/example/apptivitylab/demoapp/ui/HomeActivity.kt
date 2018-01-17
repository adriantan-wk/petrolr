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
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class HomeActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    companion object {
        val ACCESS_FINE_LOCATION_PERMISSIONS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        //Show dialog box to request use of GPS
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    ACCESS_FINE_LOCATION_PERMISSIONS)
//        }
//        else {
//
//
//            //val myToast = Toast.makeText(this, "Testing", Toast.LENGTH_SHORT);
//            //myToast.show();
//        }

        //Setting up toolbars, navigation bars and etc.
        toolbar = homeActivityToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = homeActivityDrawerLayout
        navView = homeActivityNavView

        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer_string, R.string.close_drawer_string)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navView.inflateMenu(R.menu.navigation_drawer_home_menu)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.homeActivityContainer, StationListFragment())
                .commit()
    }

}