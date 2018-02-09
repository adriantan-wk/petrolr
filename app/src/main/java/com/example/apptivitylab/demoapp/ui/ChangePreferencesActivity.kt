package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_blank_toolbar.*
import kotlinx.android.synthetic.main.dialog_welcome.view.*

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class ChangePreferencesActivity : AppCompatActivity() {

    companion object {
        const val USER_EXTRA = "user_object"
        const val NEW_USER_BOOLEAN_EXTRA = "new_user_boolean"

        fun newLaunchIntent(context: Context, currentUser: User, isNewUser: Boolean): Intent {
            val intent = Intent(context, ChangePreferencesActivity::class.java)

            intent.putExtra(USER_EXTRA, currentUser)
            intent.putExtra(NEW_USER_BOOLEAN_EXTRA, isNewUser)

            return intent
        }
    }

    private var isNewUser: Boolean = false
    private lateinit var changePreferencesFragment: ChangePreferencesFragment

    private lateinit var currentUser: User
    var unsavedChangesMade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_toolbar)

        this.isNewUser = intent.getBooleanExtra(NEW_USER_BOOLEAN_EXTRA, false)

        setSupportActionBar(this.toolbar)

        val supportActionBarTitle: String
        if (this.isNewUser) {
            supportActionBarTitle = getString(R.string.set_preferences_title)
            this.displayFirstTimePreferencesDialog()
        } else {
            supportActionBarTitle = getString(R.string.preferences_title)
            this.toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
            this.toolbar.setNavigationOnClickListener(View.OnClickListener {
                if (unsavedChangesMade) {
                    this.displayChangesNotSavedDialog()
                } else {
                    this.finish()
                }
            })
        }

        this.supportActionBar?.title = supportActionBarTitle

        this.currentUser = intent.getParcelableExtra<User>(USER_EXTRA)

        this.changePreferencesFragment = ChangePreferencesFragment.newInstance(this.currentUser)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, this.changePreferencesFragment)
                .commit()
    }

    private fun displayFirstTimePreferencesDialog() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_welcome, null)

        val alertDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .show()

        dialogView.startButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun displayChangesNotSavedDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.preferences_not_saved_warning)
                .setPositiveButton(R.string.yes, { dialog, which ->
                    this.finish()
                })
                .setNegativeButton(R.string.no, null)
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.change_preferences_save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.save) {
            this.changePreferencesFragment.updateUserPreferences(this.currentUser)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (this.isNewUser) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.preferences_not_saved_warning)
                    .setPositiveButton(R.string.yes,
                            { dialog, which ->
                                this.finish()
                            })
                    .setNegativeButton(R.string.no, null)
                    .show()
        } else {
            if (unsavedChangesMade) {
                this.displayChangesNotSavedDialog()
            } else {
                super.onBackPressed()
            }
        }
    }
}