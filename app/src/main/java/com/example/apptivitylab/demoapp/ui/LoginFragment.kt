package com.example.apptivitylab.demoapp.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.android.volley.NoConnectionError
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject
import java.net.SocketException

/**
 * Created by ApptivityLab on 09/01/2018.
 */

class LoginFragment : Fragment(), RestAPIClient.OnVerificationCompletedListener {

    companion object {
        const val NOT_USER = -1
        const val EXISTING_USER = 0
        const val NEW_USER = 1
        const val VERIFY_PATH = "/identity/session"

        const val SET_PREFERENCES_REQUEST_CODE = 201

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private var errorSnackbar: Snackbar? = null
    private var loadingDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.registerLink.setOnClickListener {
            this.activity?.let {
                it.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.titleContainer, RegisterFragment.newInstance())
                        .addToBackStack(RegisterFragment::class.java.simpleName)
                        .commit()
            }
        }

        this.loginBtn.setOnClickListener {
            this.messageTextView.text = ""

            val inputMethodManager = this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            this.showLoadingDialog()

            if (this.usernameEditText.isEmpty() || this.passwordEditText.isEmpty()) {
                this.messageTextView.text = getString(R.string.username_or_password_empty_message)
            } else {
                this.verifyUserLogin(this.usernameEditText, this.passwordEditText, this)
            }
        }

        this.forgotPassTextView.setOnClickListener {
            this.activity?.let {
                it.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.titleContainer, ForgotPasswordFragment.newInstance())
                        .addToBackStack(ForgotPasswordFragment::class.java.simpleName)
                        .commit()
            }
        }
    }

    private fun verifyUserLogin(usernameEditText: TextInputEditText, passwordEditText: TextInputEditText,
                                onVerificationCompletedListener: RestAPIClient.OnVerificationCompletedListener) {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        var jsonRequest: JSONObject = JSONObject()
        jsonRequest.put("identifier", username)
        jsonRequest.put("challenge", password)
        jsonRequest.put("type", "userpass")

        RestAPIClient.shared(this.context!!).postResources(VERIFY_PATH, jsonRequest,
                object : RestAPIClient.OnPostResponseReceivedListener {
                    override fun onPostResponseReceived(jsonObject: JSONObject?, error: VolleyError?) {
                        this@LoginFragment.hideLoadingDialog()

                        if (jsonObject != null) {
                            this@LoginFragment.errorSnackbar?.let {
                                it.dismiss()
                            }

                            if (jsonObject.has("success") && jsonObject.optString("success") == "true") {
                                val user = User(jsonObject.optJSONObject("profile"))
                                UserController.setCurrentUser(user)

                                if (UserController.user.preferredPetrolType == null || UserController.user.preferredBrands.isEmpty()) {
                                    onVerificationCompletedListener.onVerificationCompleted(NEW_USER)
                                } else {
                                    onVerificationCompletedListener.onVerificationCompleted(EXISTING_USER)
                                }
                            }
                        } else {
                            error?.let {
                                when (it) {
                                    is NoConnectionError, is SocketException -> {
                                        view?.let { view ->
                                            this@LoginFragment.errorSnackbar = Snackbar.make(view, getString(R.string.error_communicating), Snackbar.LENGTH_INDEFINITE)
                                        }

                                        this@LoginFragment.errorSnackbar?.let { snackbar ->
                                            snackbar.show()
                                        }
                                    }
                                    else -> {
                                        this@LoginFragment.errorSnackbar?.let { snackbar ->
                                            snackbar.dismiss()
                                        }

                                        this@LoginFragment.messageTextView.text = getString(R.string.login_failed)
                                        onVerificationCompletedListener.onVerificationCompleted(NOT_USER)
                                    }
                                }
                            }

                        }
                    }
                })
    }

    override fun onVerificationCompleted(resultCode: Int) {
        when (resultCode) {
            NEW_USER -> {
                val preferencesIntent = ChangePreferencesActivity.newLaunchIntent(this.context!!, true)
                startActivityForResult(preferencesIntent, SET_PREFERENCES_REQUEST_CODE)
            }
            EXISTING_USER -> {
                val randomIntent = TrackNearActivity.newLaunchIntent(this.context!!, true)
                startActivity(randomIntent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SET_PREFERENCES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newUserPreferences = data?.getParcelableExtra<User>(getString(R.string.change_preferences_intent))

            newUserPreferences?.let {
                UserController.user.preferredPetrolType = it.preferredPetrolType
                UserController.user.preferredBrands = it.preferredBrands
            }

            val randomIntent = TrackNearActivity.newLaunchIntent(this.context!!, true)
            startActivity(randomIntent)
        }
    }

    private fun showLoadingDialog() {
        if (this.loadingDialog == null) {

            this.loadingDialog = Dialog(this.activity)

            this.loadingDialog?.let {
                it.setContentView(R.layout.dialog_loading)
                it.window.setBackgroundDrawableResource(android.R.color.transparent)
                it.show()
            }
        } else {
            this.loadingDialog?.let {
                it.show()
            }
        }
    }

    private fun hideLoadingDialog() {
        this.loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun TextInputEditText.isEmpty(): Boolean {
        return this.text.toString().isBlank()
    }
}