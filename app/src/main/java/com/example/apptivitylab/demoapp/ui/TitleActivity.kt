package com.example.apptivitylab.demoapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apptivitylab.demoapp.R

class TitleActivity : AppCompatActivity() {

    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        this.loginFragment = LoginFragment.newInstance()

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.titleContainer, loginFragment)
                .commit()
    }
}
