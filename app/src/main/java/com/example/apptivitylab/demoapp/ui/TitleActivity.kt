package com.example.apptivitylab.demoapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserListController
import com.example.apptivitylab.demoapp.models.User

class TitleActivity : AppCompatActivity(), RestAPIClient.OnFullDataReceivedListener {

    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        this.loadAppData()
        this.loadAllMockData()

        this.loginFragment = LoginFragment.newInstance(UserListController.allUserList)

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.titleContainer, loginFragment)
                .commit()
    }

    private fun loadAllMockData() {
        if (UserListController.performMockDataLoad) {
            UserListController.loadMockUsers(this)
        }
    }

    private fun loadAppData() {
        BrandController.loadBrands(this, this)
        PetrolTypeController.loadPetrolTypes(this, this)
        StationController.loadStations(this, this)
    }

    fun registerNewUser(user: User) {
        UserListController.addNewUser(user)
        this.loginFragment.refreshUserList(UserListController.allUserList)
    }

    override fun onFullDataReceived(dataReceived: Boolean, error: VolleyError?) {
        if (!dataReceived || error != null) {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.warning))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(getString(R.string.failed_retrieve_data))
                    .setPositiveButton(getString(R.string.ok),
                            { dialog, which ->
                                this.finish()
                            })
                    .show()
        }
    }
}
