package com.example.apptivitylab.demoapp.ui

import android.app.Activity
import android.content.Intent
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
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Created by ApptivityLab on 09/01/2018.
 */

class LoginFragment : Fragment() {

    companion object {
        const val SET_PREFERENCES_REQUEST_CODE = 201
    }

    private lateinit var allUsersList: ArrayList<User>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.registerBtn.setOnClickListener {
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.titleContainer, RegisterFragment())
                    .addToBackStack(LoginFragment::class.java.simpleName)
                    .commit()
            }
        }

        this.loginBtn.setOnClickListener {
            if (this.usernameEditText.isEmpty() || this.passwordEditText.isEmpty()) {
                this.messageTextView.text = getString(R.string.username_or_password_empty_message)
            } else {
                if (this.isUserLoginDetailsCorrect(this.usernameEditText, this.passwordEditText)) {
                    if (UserController.user.preferredPetrolType == null || UserController.user.preferredBrands.isEmpty()) {
                        val preferencesIntent = ChangePreferencesActivity.newLaunchIntent(this.context!!, UserController.user, true)
                        startActivityForResult(preferencesIntent, SET_PREFERENCES_REQUEST_CODE)
                    } else {
                        val randomIntent = TrackNearActivity.newLaunchIntent(this.context!!, StationController.stationList)
                        startActivity(randomIntent)
                    }
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
                return if (user.password == password) {
                    UserController.setCurrentUser(user)
                    true
                } else {
                    false
                }
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SET_PREFERENCES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newUserPreferences = data?.getParcelableExtra<User>(getString(R.string.change_preferences_intent))

            newUserPreferences?.let {
                UserController.user.preferredPetrolType = it.preferredPetrolType
                UserController.user.preferredBrands = it.preferredBrands
            }

            val randomIntent = TrackNearActivity.newLaunchIntent(this.context!!, StationController.stationList)
            startActivity(randomIntent)
        }
    }

    fun TextInputEditText.isEmpty(): Boolean {
        return this.text.toString() == ""
    }
}