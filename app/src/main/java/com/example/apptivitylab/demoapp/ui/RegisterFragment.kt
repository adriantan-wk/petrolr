package com.example.apptivitylab.demoapp.ui

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.apptivitylab.demoapp.R
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * Created by ApptivityLab on 11/01/2018.
 */
class RegisterFragment : Fragment() {
    private lateinit var username:TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var confirmPass:TextInputEditText
    private lateinit var registerBtn:Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = rusernameEditText
        password = passwordEditText
        confirmPass = confirmPassEditText
        registerBtn = registerBtn

        //TODO True registration functionality
        registerBtn.setOnClickListener({
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.titleContainer, LoginFragment())
                    .commit()
            }
        })
    }
}