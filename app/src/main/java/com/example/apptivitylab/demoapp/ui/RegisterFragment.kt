package com.example.apptivitylab.demoapp.ui

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.NoConnectionError
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONObject
import java.net.SocketException

/**
 * Created by ApptivityLab on 11/01/2018.
 */
class RegisterFragment : Fragment(), RestAPIClient.OnVerificationCompletedListener {

    companion object {
        const val REGISTERED = 0
        const val REGISTER_PATH="/identity"

        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }

    private var errorSnackbar: Snackbar? = null
    private var loadingDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.registerBtn.setOnClickListener({
            this.validateRegistration(this.usernameEditText, this.emailEditText,
                    this.phoneNoEditText, this.passwordEditText, this.confirmPassEditText, this)
        })

        this.loginLink.setOnClickListener {
            this.activity?.let {
                it.supportFragmentManager
                        .popBackStackImmediate()
            }
        }
    }

    private fun validateRegistration(usernameEditText: TextInputEditText, emailEditText: TextInputEditText, phoneNoEditText: TextInputEditText,
                                     passwordEditText: TextInputEditText, confirmPassEditText: TextInputEditText,
                                     onVerificationCompletedListener: RestAPIClient.OnVerificationCompletedListener) {
        if (!this.isEmptyFieldsFound()) {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phoneNo = phoneNoEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPassEditText.text.toString()

            if (this.isFieldsValid(email, phoneNo, password, confirmPassword)) {
                var jsonRequest = JSONObject()
                jsonRequest.put("identifier", username)
                jsonRequest.put("challenge", password)
                jsonRequest.put("type", "userpass")
                jsonRequest.put("name", username)
                jsonRequest.put("email", email)
                jsonRequest.put("phone", phoneNo)

                this.showLoadingDialog()

                RestAPIClient.shared(this.context!!).postResources(REGISTER_PATH, jsonRequest,
                        object : RestAPIClient.OnPostResponseReceivedListener {
                            override fun onPostResponseReceived(jsonObject: JSONObject?, error: VolleyError?) {
                                this@RegisterFragment.hideLoadingDialog()

                                if (jsonObject != null) {
                                    if (jsonObject.has("success") && jsonObject.optString("success") == "true") {
                                        onVerificationCompletedListener.onVerificationCompleted(REGISTERED)

                                    }
                                } else {
                                    error?.let {
                                        when (it) {
                                            is NoConnectionError, is SocketException -> {
                                                view?.let {view ->
                                                    this@RegisterFragment.errorSnackbar = Snackbar.make(view, getString(R.string.error_communicating), Snackbar.LENGTH_INDEFINITE)
                                                }

                                                this@RegisterFragment.errorSnackbar?.let { snackbar ->
                                                    snackbar.show()
                                                }
                                            }
                                            else -> {
                                                this@RegisterFragment.errorSnackbar?.let { snackbar ->
                                                    snackbar.dismiss()
                                                }

                                                this@RegisterFragment.usernameTextInputLayout.error = getString(R.string.username_exists)
                                            }
                                        }
                                    }
                                }
                            }
                        })
            }
        }
    }

    override fun onVerificationCompleted(resultCode: Int) {
        if (resultCode == REGISTERED) {
            Toast.makeText(this@RegisterFragment.context!!, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()

            this@RegisterFragment.activity?.let {
                it.supportFragmentManager
                        .popBackStackImmediate()
            }
        }
    }

    private fun isEmptyFieldsFound(): Boolean {
        this.usernameTextInputLayout.error = null
        this.emailTextInputLayout.error = null
        this.phoneNoTextInputLayout.error = null
        this.passwordTextInputLayout.error = null
        this.confirmPasswordTextInputLayout.error = null

        return this.usernameTextInputLayout.isEmpty() || this.emailTextInputLayout.isEmpty() || this.phoneNoTextInputLayout.isEmpty()
                || this.passwordTextInputLayout.isEmpty() || this.confirmPasswordTextInputLayout.isEmpty()
    }

    private fun isFieldsValid(email: String, phoneNo: String, password: String, confirmPassword: String): Boolean {
        var valid = false

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (android.util.Patterns.PHONE.matcher(phoneNo).matches()) {
                if (password == confirmPassword) {
                    valid = true
                } else {
                    this.passwordTextInputLayout.error = getString(R.string.password_and_confirm_must_match)
                    this.confirmPasswordTextInputLayout.error = getString(R.string.password_and_confirm_must_match)
                }
            } else {
                this.phoneNoTextInputLayout.error = getString(R.string.invalid_phone_no)
            }
        } else {
            this.emailTextInputLayout.error = getString(R.string.invalid_email_address)
        }

        return valid
    }

    private fun showLoadingDialog() {
        if (this.loadingDialog == null) {

            this.loadingDialog = Dialog(this.activity)

            this.loadingDialog?.let {
                it.setContentView(R.layout.dialog_loading)
                it.window.setBackgroundDrawableResource(android.R.color.transparent)
                it.progressBarTextView.text = getString(R.string.registering)
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

    private fun TextInputLayout.isEmpty(): Boolean {
        return if (this.editText?.text.toString().isBlank()) {
            this.error = this.context.getString(R.string.field_must_not_be_empty, this.hint)
            true
        } else {
            false
        }
    }
}