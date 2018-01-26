package com.example.apptivitylab.demoapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_station_list.*

/**
 * Created by ApptivityLab on 17/01/2018.
 */

class StationListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val CHANGE_PREFERENCES_REQUEST_CODE = 200
        const val USER_EXTRA = "user_object"
        const val STATION_LIST_EXTRA = "station_list"

        fun newLaunchIntent(context: Context, stations: ArrayList<Station>): Intent {
            val intent = Intent(context, StationListActivity::class.java)
            intent.putExtra(STATION_LIST_EXTRA, stations)

            return intent
        }
    }

    private lateinit var stationListFragment: StationListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_list)

        setSupportActionBar(stationListToolbar)
        supportActionBar?.title = getString(R.string.station_list_title_string)

        val drawerToggle = ActionBarDrawerToggle(this, stationListDrawerLayout, stationListToolbar,
                R.string.open_drawer_string, R.string.close_drawer_string)
        stationListDrawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        stationListNavView.inflateMenu(R.menu.navigation_drawer_home_menu)
        stationListNavView.setNavigationItemSelectedListener(this)

        val stations = intent.getParcelableArrayListExtra<Station>(STATION_LIST_EXTRA)

        this.stationListFragment = StationListFragment.newInstance(UserController.user, stations)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrameLayout, this.stationListFragment)
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

    override fun onNavigationItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
        //TODO Other side drawer menu items

            R.id.nav_track_nearby -> {
                val trackNearIntent = TrackNearActivity.newLaunchIntent(this, StationController.stationList)
                startActivity(trackNearIntent)


                stationListDrawerLayout.closeDrawers()
                true
            }

            R.id.nav_station_list -> {
                stationListDrawerLayout.closeDrawers()
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

        stationListFragment.onUserPreferencesChanged(user)
    }
}