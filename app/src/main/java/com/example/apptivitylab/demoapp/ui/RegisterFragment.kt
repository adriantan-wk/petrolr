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

        //TODO True registration functionality
        registerBtn.setOnClickListener({
            if (this.isEmptyFieldsFound()) {
                this.messageTextView.text = getString(R.string.registration_failed_fields_empty)
            } else {
                val username = this.usernameEditText.text.toString()
                val password = this.passwordEditText.text.toString()
                val confirmPassword = this.confirmPassEditText.text.toString()

                if (this.usernameExists(username)) {
                    this.messageTextView.text = getString(R.string.username_exists)
                } else {
                    if (password == confirmPassword) {
                        val titleActivity = this.activity as TitleActivity
                        titleActivity.registerNewUser(this.createNewUser(username, password))

                        Toast.makeText(this.context!!, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                        this.activity?.let {
                            it.supportFragmentManager
                                    .popBackStackImmediate()
                        }
                    } else {
                        this.messageTextView.text = getString(R.string.confirm_password_does_not_match)
                    }
                }
            }
        })
    }

    private fun isEmptyFieldsFound(): Boolean {
        this.usernameTextInputLayout.error = null
        this.passwordTextInputLayout.error = null
        this.confirmPasswordTextInputLayout.error = null

        return this.usernameTextInputLayout.isEmpty() || this.passwordTextInputLayout.isEmpty() || this.confirmPasswordTextInputLayout.isEmpty()
    }

    private fun usernameExists(username: String): Boolean {
        this.userList.forEach { user ->
            if (user.username == username) {
                return true
            }
        }
        return false
    }

    private fun createNewUser(username: String, password: String): User {
        val user = User()
        user.username = username
        user.password = password
        user.preferredPetrolType = null
        user.preferredBrands = ArrayList()

        return user
    }

    fun TextInputLayout.isEmpty(): Boolean {
        return if (this.editText?.text.toString() == "") {
            this.error = this.context.getString(R.string.field_must_not_be_empty, this.hint)
            true
        } else {
            false
        }
    }
}