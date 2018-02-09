package com.example.apptivitylab.demoapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.activity_blank_toolbar.*
import kotlinx.android.synthetic.main.activity_petrol_price_history.*

/**
 * Created by ApptivityLab on 31/01/2018.
 */

class PetrolPriceHistoryActivity : AppCompatActivity() {

    companion object {
        const val USER_EXTRA = "user_object"
        const val PETROL_TYPE_LIST_EXTRA = "petrol_type_list"

        fun newLaunchIntent(context: Context, currentUser: User, petrolTypeList: ArrayList<PetrolType>): Intent {
            val intent = Intent(context, PetrolPriceHistoryActivity::class.java)

            intent.putExtra(USER_EXTRA, currentUser)
            intent.putExtra(PETROL_TYPE_LIST_EXTRA, petrolTypeList)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petrol_price_history)

        setSupportActionBar(this.priceHistoryToolbar)
        supportActionBar?.title = getString(R.string.price_history)

        this.priceHistoryToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        this.priceHistoryToolbar.setNavigationOnClickListener (View.OnClickListener {
            finish()
        })

        val currentUser = intent.getParcelableExtra<User>(USER_EXTRA)
        val petrolTypeList = intent.getParcelableArrayListExtra<PetrolType>(PETROL_TYPE_LIST_EXTRA)

        var preferredPetrolType = PetrolType()

        petrolTypeList.forEach { petrolType ->
            if (petrolType.petrolID == currentUser.preferredPetrolType?.petrolID) {
                preferredPetrolType = petrolType
            }
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, PetrolPriceHistoryFragment.newInstance(preferredPetrolType))
                .commit()
    }
}