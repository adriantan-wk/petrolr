package com.example.apptivitylab.demoapp

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        username = registerFragmentUsername
        password = registerFragmentPass
        confirmPass = registerFragmentPassConfirm
        registerBtn = registerFragmentRegisterBtn

        registerBtn.setOnClickListener({
            activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.titleActivityContainer, LoginFragment())
                    .commit()
        })
    }
}