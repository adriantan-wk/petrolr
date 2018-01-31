package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Created by ApptivityLab on 09/01/2018.
 */

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerBtn.setOnClickListener {
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.titleContainer, RegisterFragment())
                    .addToBackStack(LoginFragment::class.java.simpleName)
                    .commit()
            }
        }

        //TODO True login verification functionality
        loginBtn.setOnClickListener {
            UserController.createMockUser()
            StationController.loadMockStations(this.context!!)
            PetrolTypeController.loadMockPetrolTypes(this.context!!)

            val randomIntent = TrackNearActivity.newLaunchIntent(this.context!!, StationController.stationList)
            startActivity(randomIntent)
        }
    }
}