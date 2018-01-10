package com.example.apptivitylab.demoapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Gravity
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_search.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.loginActivityContainer, LoginFragment())
                .commit()
    }
}
