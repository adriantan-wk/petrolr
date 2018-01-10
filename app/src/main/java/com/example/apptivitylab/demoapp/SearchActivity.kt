package com.example.apptivitylab.demoapp

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_search.*

/**
 * Created by ApptivityLab on 10/01/2018.
 */

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(searchActivityToolbar)
        searchActivityToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)

        //TODO Replace with Search Location Fragment
    }
}