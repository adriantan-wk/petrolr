package com.example.apptivitylab.demoapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.apptivitylab.demoapp.R
import kotlinx.android.synthetic.main.activity_change_preferences.*
import kotlinx.android.synthetic.main.activity_station_list.*

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class ChangePreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_preferences)

        setSupportActionBar(changePrefToolbar)
        supportActionBar?.title = getString(R.string.change_preferences_title_string)

        changePrefToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        changePrefToolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.changePrefContainer, ChangePreferencesFragment())
                .commit()
    }
}