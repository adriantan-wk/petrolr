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
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_station_list.*
import kotlinx.android.synthetic.main.nav_view_header.view.*

/**
 * Created by ApptivityLab on 17/01/2018.
 */

class StationListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val CHANGE_PREFERENCES_REQUEST_CODE = 200
        const val FROM_SEE_MORE_EXTRA = "see_more"

        fun newLaunchIntent(context: Context, isFromSeeMore: Boolean): Intent {
            val intent = Intent(context, StationListActivity::class.java)
            intent.putExtra(FROM_SEE_MORE_EXTRA, isFromSeeMore)

            return intent
        }
    }

    private lateinit var stationListFragment: StationListFragment
    private lateinit var stationList: ArrayList<Station>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_list)

        setSupportActionBar(this.stationListToolbar)
        supportActionBar?.title = getString(R.string.station_list_title)

        val isFromSeeMore = intent.getBooleanExtra(FROM_SEE_MORE_EXTRA, false)

        if (isFromSeeMore) {
            this.stationListToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
            this.stationListToolbar.setNavigationOnClickListener(View.OnClickListener {
                finish()
            })
        } else {
            val drawerToggle = ActionBarDrawerToggle(this, stationListDrawerLayout, stationListToolbar,
                    R.string.open_drawer, R.string.close_drawer)
            this.stationListDrawerLayout.addDrawerListener(drawerToggle)
            drawerToggle.syncState()

            this.stationListNavView.inflateMenu(R.menu.navigation_drawer_home_menu)
            this.stationListNavView.setNavigationItemSelectedListener(this)
        }

        val navigationViewHeader: View = this.stationListNavView.getHeaderView(0)
        navigationViewHeader.navHeaderUserTextView.text = UserController.user.username
        navigationViewHeader.navHeaderEmailTextView.text = UserController.user.email

        this.stationList = StationController.stationList

        this.stationListFragment = StationListFragment.newInstance(UserController.user)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFrameLayout, this.stationListFragment)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHANGE_PREFERENCES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newUserPreferences = data?.getParcelableExtra<User>(getString(R.string.change_preferences_intent))

            this.stationListDrawerLayout.closeDrawers()

            newUserPreferences?.let {
                updateUserPreferences(newUserPreferences)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_track_nearby -> {
                val trackNearIntent = TrackNearActivity.newLaunchIntent(this, BrandController.brandList)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(trackNearIntent)

                this.stationListDrawerLayout.closeDrawers()
                true
            }

            R.id.nav_station_list -> {
                this.stationListDrawerLayout.closeDrawers()
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

                true
            }

            else -> {
                val toast = Toast.makeText(this, R.string.feature_unavailable, Toast.LENGTH_SHORT)
                toast.show()

                false
            }
        }
    }

    private fun updateUserPreferences(user: User) {
        UserController.user.preferredPetrolType = user.preferredPetrolType
        UserController.user.preferredBrands = user.preferredBrands

        this.stationListFragment.onUserPreferencesChanged(user)
    }
}