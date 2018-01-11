package com.example.apptivitylab.demoapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

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
