package com.example.apptivitylab.demoapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.apptivitylab.demoapp.R
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Created by ApptivityLab on 09/01/2018.
 */

class LoginFragment : Fragment() {
    private lateinit var username:TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var loginBtn:Button
    private lateinit var registerBtn:Button
    private lateinit var forgotPass:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = loginFragmentUsername
        password = loginFragmentPassword
        loginBtn = loginFragmentLoginBtn
        registerBtn = loginFragmentRegisterBtn
        forgotPass = loginFragmentForgotPass

        registerBtn.setOnClickListener {
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.titleActivityContainer, RegisterFragment())
                    .addToBackStack("Login to Register")
                    .commit()
            }
        }

        //TODO True login verification functionality
        loginBtn.setOnClickListener {
            val randomIntent = Intent(context, HomeActivity::class.java)

            startActivity(randomIntent)
        }
    }
}