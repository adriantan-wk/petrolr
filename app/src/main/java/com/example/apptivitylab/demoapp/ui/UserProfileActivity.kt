package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_petrol_price_history.*

/**
 * Created by ApptivityLab on 23/02/2018.
 */

class UserProfileActivity : AppCompatActivity() {

    companion object {
        const val USER_EXTRA = "user_object"

        fun newLaunchIntent(context: Context, currentUser: User): Intent {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(USER_EXTRA, currentUser)

            return intent
        }
    }

    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petrol_price_history)

        setSupportActionBar(this.priceHistoryToolbar)
        this.supportActionBar?.title = ""

        this.priceHistoryToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        this.priceHistoryToolbar.setNavigationOnClickListener({
            finish()
        })

        this.currentUser = intent.getParcelableExtra(USER_EXTRA)

        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, UserProfileFragment.newInstance(this, this.currentUser))
                .commit()
    }
}