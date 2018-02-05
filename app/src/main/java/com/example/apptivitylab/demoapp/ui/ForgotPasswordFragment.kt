package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.dialog_password.view.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*

/**
 * Created by ApptivityLab on 05/02/2018.
 */

class ForgotPasswordFragment : Fragment() {

    companion object {
        const val USER_LIST_EXTRA = "user_list"

        fun newInstance(userList: ArrayList<User>): ForgotPasswordFragment {
            val fragment = ForgotPasswordFragment()

            val args: Bundle = Bundle()
            args.putParcelableArrayList(USER_LIST_EXTRA, userList)

            fragment.arguments = args
            return fragment
        }
    }

    private var userList: ArrayList<User> = ArrayList()
    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.arguments?.let {
            this.userList = it.getParcelableArrayList(USER_LIST_EXTRA)
        }

        this.okButton.setOnClickListener {
            this.usernameTextInputLayout.error = ""
            this.emailTextInputLayout.error = ""

            if (validateUsername(this.usernameEditText) && validateEmail(this.emailEditText)) {
                    this.displayPasswordDialog()
            }
        }
    }

    private fun validateUsername(username: TextInputEditText): Boolean {
        val username = username.text.toString()

        return if (username == "") {
            this.usernameTextInputLayout.error = getString(R.string.enter_username)
            false
        } else {
            val user = this.userList.firstOrNull { user ->
                user.username == username
            }

            if (user == null) {
                this.usernameTextInputLayout.error = getString(R.string.no_such_username)
                false
            } else {
                this.user = user
                true
            }
        }
    }

    private fun validateEmail(email: TextInputEditText): Boolean {
        val email = email.text.toString()

        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.user?.let {
                return if (it.email == email) {
                    true
                } else {
                    this.emailTextInputLayout.error = getString(R.string.email_does_not_match_recorded)
                    false
                }
            }
            false
        } else {
            this.emailTextInputLayout.error = getString(R.string.invalid_email_address)
            false
        }
    }

    private fun displayPasswordDialog() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_password, null)

        dialogView.passwordTextView.text = this.user?.password

        val passwordDialog = AlertDialog.Builder(this.context!!)
                .setView(dialogView)
                .show()

        dialogView.okButton.setOnClickListener {
            passwordDialog.dismiss()

            this.activity?.let {
                it.supportFragmentManager
                        .popBackStackImmediate()
            }
        }
    }
}