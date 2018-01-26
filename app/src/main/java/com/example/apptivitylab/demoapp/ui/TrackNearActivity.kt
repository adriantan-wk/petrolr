package com.example.apptivitylab.demoapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import com.example.apptivitylab.demoapp.R
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.apptivitylab.demoapp.R.raw.stations
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_track_nearby.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val CHANGE_PREFERENCES_REQUEST_CODE = 200
        const val USER_EXTRA = "user_object"
        const val STATION_LIST_EXTRA = "station_list"

        fun newLaunchIntent(context: Context, stations: ArrayList<Station>): Intent {
            val intent = Intent(context, TrackNearActivity::class.java)
            intent.putExtra(STATION_LIST_EXTRA, stations)

            return intent
        }
    }

    private lateinit var stations: ArrayList<Station>

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
        trackNearNavView.setNavigationItemSelectedListener(this)

        this.stations = intent.getParcelableArrayListExtra<Station>(STATION_LIST_EXTRA)

        supportFragmentManager
        .beginTransaction()
        .replace(R.id.containerFrameLayout, TrackNearbyFragment.newInstance(stations))
        .commit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CHANGE_PREFERENCES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val newUserPreferences = data?.getParcelableExtra<User>(getString(R.string.change_preferences_intent_string))

                newUserPreferences?.let {
                    updateUserPreferences(newUserPreferences)
                }
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return  when (item.itemId) {
        //TODO Other side drawer menu items

            R.id.nav_track_nearby -> {
                trackNearDrawerLayout.closeDrawers()
                true
            }

            R.id.nav_station_list -> {
                val stationListIntent = StationListActivity.newLaunchIntent(this, this.stations)
                startActivity(stationListIntent)

                trackNearDrawerLayout.closeDrawers()
                true
            }

            R.id.nav_preferences -> {
                val preferencesIntent = ChangePreferencesActivity.newLaunchIntent(this, UserController.user)
                startActivityForResult(preferencesIntent, CHANGE_PREFERENCES_REQUEST_CODE)

                true
            }

            R.id.nav_logout -> {
                AlertDialog.Builder(this)
                        .setIcon(R.drawable.logout)
                        .setTitle(R.string.logout_dialog_title_string)
                        .setMessage(R.string.logout_confirm_msg_string)
                        .setPositiveButton(R.string.yes_string,
                                { dialog, which ->
                                    val logOutIntent = Intent(this, TitleActivity::class.java)
                                    startActivity(logOutIntent)
                                })
                        .setNegativeButton(R.string.no_string, null)
                        .show()

                true
            }

            else -> {
                val toast = Toast.makeText(this, R.string.feature_unavailable_string, Toast.LENGTH_SHORT)
                toast.show()

                false
            }
        }
    }

    private fun updateUserPreferences(user: User) {
        UserController.user.preferredPetrolType = user.preferredPetrolType
        UserController.user.preferredBrands = user.preferredBrands

        //TODO Update fragment that user preferences have changed
    }

}