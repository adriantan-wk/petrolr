package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_blank_toolbar.*

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class ChangePreferencesActivity : AppCompatActivity() {

    companion object {
        const val USER_EXTRA = "user_object"

        fun newLaunchIntent(context: Context, currentUser: User): Intent {
            val intent = Intent(context, ChangePreferencesActivity::class.java)

            intent.putExtra(USER_EXTRA, currentUser)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_toolbar)

        setSupportActionBar(this.toolbar)
        supportActionBar?.title = getString(R.string.change_preferences_title)

        this.toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        this.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        val user = intent.getParcelableExtra<User>(USER_EXTRA)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, ChangePreferencesFragment.newInstance(user))
                .commit()
    }
}