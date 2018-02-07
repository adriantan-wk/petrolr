package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * Created by ApptivityLab on 11/01/2018.
 */
class RegisterFragment : Fragment() {

    companion object {
        const val USER_LIST_EXTRA = "user_list"

        fun newInstance(userList: ArrayList<User>): RegisterFragment {
            val fragment = RegisterFragment()

            val args: Bundle = Bundle()
            args.putParcelableArrayList(USER_LIST_EXTRA, userList)

            fragment.arguments = args
            return fragment
        }
    }

    var userList: ArrayList<User> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.arguments?.let {
            this.userList = it.getParcelableArrayList(USER_LIST_EXTRA)
        }

        this.registerBtn.setOnClickListener({
            this.validateRegistration()
        })

        this.loginLink.setOnClickListener {
            this.activity?.let {
                it.supportFragmentManager
                        .popBackStackImmediate()
            }
        }
    }

    private fun validateRegistration() {
        if (!this.isEmptyFieldsFound()) {
            val username = this.usernameEditText.text.toString()
            val email = this.emailEditText.text.toString()
            val password = this.passwordEditText.text.toString()
            val confirmPassword = this.confirmPassEditText.text.toString()

            if (this.usernameExists(username)) {
                this.usernameTextInputLayout.error = getString(R.string.username_exists)
            } else {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (password == confirmPassword) {
                        val titleActivity = this.activity as TitleActivity
                        titleActivity.registerNewUser(this.createNewUser(username, email, password))

                        Toast.makeText(this.context!!, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                        this.activity?.let {
                            it.supportFragmentManager
                                    .popBackStackImmediate()
                        }
                    } else {
                        this.passwordTextInputLayout.error = getString(R.string.password_and_confirm_must_match)
                        this.confirmPasswordTextInputLayout.error = getString(R.string.password_and_confirm_must_match)
                    }
                } else {
                    this.emailTextInputLayout.error = getString(R.string.invalid_email_address)
                }
            }
        }
    }

    private fun isEmptyFieldsFound(): Boolean {
        this.usernameTextInputLayout.error = null
        this.emailTextInputLayout.error = null
        this.passwordTextInputLayout.error = null
        this.confirmPasswordTextInputLayout.error = null

        return this.usernameTextInputLayout.isEmpty() || this.emailTextInputLayout.isEmpty()
                || this.passwordTextInputLayout.isEmpty() || this.confirmPasswordTextInputLayout.isEmpty()
    }

    private fun usernameExists(username: String): Boolean {
        val user = this.userList.firstOrNull { user ->
            user.username == username
        }

        return user != null
    }

    private fun createNewUser(username: String, email: String, password: String): User {
        val user = User()
        user.username = username
        user.password = password
        user.email = email
        user.preferredPetrolType = null
        user.preferredBrands = ArrayList()

        return user
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