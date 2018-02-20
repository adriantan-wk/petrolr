package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.R.string.brand
import com.example.apptivitylab.demoapp.api.RestAPIClient
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController.user
import com.example.apptivitylab.demoapp.controllers.UserListController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.User
import org.json.JSONObject

class TitleActivity : AppCompatActivity() {

    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        BrandController.loadBrands(this)
        PetrolTypeController.loadPetrolTypes(this)
        StationController.loadStations(this)

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

    fun registerNewUser(user: User) {
        UserListController.addNewUser(user)
        this.loginFragment.refreshUserList(UserListController.allUserList)
    }
}
