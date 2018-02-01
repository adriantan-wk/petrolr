package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.controllers.StationController
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.controllers.UserController.user
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Created by ApptivityLab on 09/01/2018.
 */

class LoginFragment : Fragment() {

    private lateinit var allUsersList: ArrayList<User>

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

        loginBtn.setOnClickListener {
            if (this.usernameEditText.isEmpty() || this.passwordEditText.isEmpty()) {
                this.messageTextView.text = getString(R.string.fields_empty)
            } else {
                if (this.isUserLoginDetailsCorrect(this.usernameEditText, this.passwordEditText)) {
                    //TODO Check for first time preferences setting
                    val randomIntent = TrackNearActivity.newLaunchIntent(this.context!!, StationController.stationList)
                    startActivity(randomIntent)
                } else {
                    this.messageTextView.text = getString(R.string.login_failed)
                }
            }
        }

        this.loadAllMockData()
    }

    private fun loadAllMockData() {
        StationController.loadMockStations(this.context!!)
        PetrolTypeController.loadMockPetrolTypes(this.context!!)
        this.allUsersList = UserController.loadMockUsers(this.context!!)
    }

    private fun isUserLoginDetailsCorrect(usernameEditText: TextInputEditText, passwordEditText: TextInputEditText): Boolean {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        allUsersList.forEach { user ->
            if (user.username == username) {
                if (user.password == password) {
                    UserController.setCurrentUser(user)
                    return true
                } else {
                    return false
                }
            }
        }
        return false
    }

    fun TextInputEditText.isEmpty(): Boolean {
        return this.text.toString() == ""
    }
}