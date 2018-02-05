package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.controllers.UserListController
import com.example.apptivitylab.demoapp.models.User

class TitleActivity : AppCompatActivity() {

    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        this.loadAllMockData()

        this.loginFragment = LoginFragment.newInstance(UserListController.allUserList)

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.titleContainer, loginFragment)
                .commit()
    }

    private fun loadAllMockData() {
        StationController.loadMockStations(this)
        PetrolTypeController.loadMockPetrolTypes(this)

        if (UserListController.performMockDataLoad) {
            UserListController.loadMockUsers(this)
        }
    }

    fun registerNewUser(user: User) {
        UserListController.addNewUser(user)
        this.loginFragment.refreshUserList(UserListController.allUserList)
    }
}
