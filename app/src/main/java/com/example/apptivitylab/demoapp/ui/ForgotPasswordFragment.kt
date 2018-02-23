package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apptivitylab.demoapp.R
import kotlinx.android.synthetic.main.dialog_password.view.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*

/**
 * Created by ApptivityLab on 05/02/2018.
 */

class ForgotPasswordFragment : Fragment() {

    companion object {
        fun newInstance(): ForgotPasswordFragment {
            return ForgotPasswordFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.okButton.setOnClickListener {
            this.validateUserDetails(this.usernameEditText, this.emailEditText)
        }
    }

    private fun validateUserDetails(usernameEditText: TextInputEditText, emailEditText: TextInputEditText) {
        if (!this.isEmptyFieldsFound()) {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                this.displayPasswordDialog()
            } else {
                this.emailTextInputLayout.error = getString(R.string.invalid_email_address)
            }
        }
    }

    private fun displayPasswordDialog() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_password, null)

        dialogView.passwordTextView.text = getString(R.string.unavailable)

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

    private fun isEmptyFieldsFound(): Boolean {
        this.usernameTextInputLayout.error = null
        this.emailTextInputLayout.error = null

        return this.usernameTextInputLayout.isEmpty() || this.emailTextInputLayout.isEmpty()
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