package com.example.apptivitylab.demoapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.FrameLayout

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //val mToolbar = findViewById<Toolbar>(R.id.activity_login_toolbar)
        //setSupportActionBar(mToolbar)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_login_vg_container, LoginFragment())
                .commit()
    }
}
