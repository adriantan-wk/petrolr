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
import android.view.View
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_track_nearby.*
import kotlinx.android.synthetic.main.nav_view_header.view.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class TrackNearActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val CHANGE_PREFERENCES_REQUEST_CODE = 200
        const val STATION_LIST_EXTRA = "station_list"
        const val BRAND_LIST_EXTRA = "brand_list"

        fun newLaunchIntent(context: Context, stations: ArrayList<Station>, brands: ArrayList<Brand>): Intent {
            val intent = Intent(context, TrackNearActivity::class.java)

            intent.putExtra(STATION_LIST_EXTRA, stations)
            intent.putExtra(BRAND_LIST_EXTRA, brands)

            return intent
        }
    }

    private lateinit var stations: ArrayList<Station>
    private lateinit var brands: ArrayList<Brand>
    private lateinit var trackNearbyFragment: TrackNearbyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_nearby)

        setSupportActionBar(this.trackNearToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerToggle = ActionBarDrawerToggle(this, this.trackNearDrawerLayout,
                this.trackNearToolbar, R.string.open_drawer, R.string.close_drawer)
        this.trackNearDrawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        this.trackNearNavView.inflateMenu(R.menu.navigation_drawer_home_menu)
        this.trackNearNavView.setNavigationItemSelectedListener(this)

        val navigationViewHeader: View = this.trackNearNavView.getHeaderView(0)
        navigationViewHeader.navHeaderUserTextView.text = UserController.user.username

        this.stations = intent.getParcelableArrayListExtra<Station>(STATION_LIST_EXTRA)
        this.brands = intent.getParcelableArrayListExtra(BRAND_LIST_EXTRA)

        this.trackNearbyFragment = TrackNearbyFragment.newInstance(UserController.user, this.stations, this.brands)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrameLayout, this.trackNearbyFragment)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHANGE_PREFERENCES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newUserPreferences = data?.getParcelableExtra<User>(getString(R.string.change_preferences_intent))

            this.trackNearDrawerLayout.closeDrawers()

            newUserPreferences?.let {
                updateUserPreferences(newUserPreferences)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_track_nearby -> {
                this.trackNearDrawerLayout.closeDrawers()
                true
            }

            R.id.nav_station_list -> {
                val stationListIntent = StationListActivity.newLaunchIntent(this, this.stations)
                startActivity(stationListIntent)

                this.trackNearDrawerLayout.closeDrawers()
                true
            }

            R.id.nav_price_history -> {
                val priceHistoryIntent = PetrolPriceHistoryActivity.newLaunchIntent(this, UserController.user, PetrolTypeController.petrolTypeList)
                startActivity(priceHistoryIntent)

                true
            }

            R.id.nav_preferences -> {
                val preferencesIntent = ChangePreferencesActivity.newLaunchIntent(this, UserController.user, false)
                startActivityForResult(preferencesIntent, CHANGE_PREFERENCES_REQUEST_CODE)

                true
            }

            R.id.nav_logout -> {
                this.displayLogOutDialog()

                true
            }

            else -> {
                val toast = Toast.makeText(this, R.string.feature_unavailable, Toast.LENGTH_SHORT)
                toast.show()

                false
            }
        }
    }

    override fun onBackPressed() {
        this.displayLogOutDialog()
    }

    private fun displayLogOutDialog() {
        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_logout)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_confirm_msg)
                .setPositiveButton(R.string.yes,
                        { dialog, which ->
                            UserController.logOutUser()

                            val logOutIntent = Intent(this, TitleActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(logOutIntent)
                        })
                .setNegativeButton(R.string.no, null)
                .show()
    }

    private fun updateUserPreferences(user: User) {
        UserController.user.preferredPetrolType = user.preferredPetrolType
        UserController.user.preferredBrands = user.preferredBrands

        this.trackNearbyFragment.onUserPreferencesChanged(user)
    }
}