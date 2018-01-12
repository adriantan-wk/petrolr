package com.example.apptivitylab.demoapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.apptivitylab.demoapp.R

class TitleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.titleActivityContainer, LoginFragment())
                .commit()
    }
}
