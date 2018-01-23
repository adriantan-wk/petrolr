package com.example.apptivitylab.demoapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import com.example.apptivitylab.demoapp.MockDataLoader
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Petrol
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_change_preferences.*

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class ChangePreferencesFragment : Fragment() {

    private var listOfPetrolTypes : ArrayList<Petrol> = ArrayList()
    private var listOfBrands : ArrayList<Brand> = ArrayList()

    private var mapOfPetrolTypeRadioBtns : HashMap<String, RadioButton> = HashMap()
    private var mapOfBrandCheckBoxes : HashMap<String, CheckBox> = HashMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.listOfPetrolTypes = MockDataLoader.loadJSONPetrolTypes(context!!)
        this.listOfBrands = MockDataLoader.loadJSONBrands(context!!)

        this.selectedPetrolTextView.text = String.format(getString(R.string.preferred_petrol_string, ""))

        this.createPetrolRadioBtns(this.mapOfPetrolTypeRadioBtns)
        this.createBrandCheckBoxes(this.mapOfBrandCheckBoxes)

        this.setCurrentUserPreferences(UserController.user)

        this.saveBtn.setOnClickListener {
            this.updateUserPreferences(UserController.user)
        }
    }

    private fun createPetrolRadioBtns(mapOfPetrolTypeRadioBtns: HashMap<String, RadioButton>) {
        this.listOfPetrolTypes.forEach { petrol ->
            val radioBtn = RadioButton(context)
            radioBtn.text = petrol.petrolName

            radioBtn.setOnClickListener {
                selectedPetrolTextView.text = String.format(getString(R.string.preferred_petrol_string, radioBtn.text))
            }
            this.petrolTypesRadioGroup.addView(radioBtn)

            petrol.petrolID?.let {
                mapOfPetrolTypeRadioBtns.put(it, radioBtn)
            }
        }
    }

    private fun createBrandCheckBoxes(mapOfBrandCheckBoxes: HashMap<String, CheckBox>) {
        this.listOfBrands.forEach { brand ->
            val chkBox = CheckBox(context)
            chkBox.text = brand.brandName
            this.brandsLinearLayout.addView(chkBox)

            brand.brandID?.let {
                mapOfBrandCheckBoxes.put(it, chkBox)
            }
        }
    }

    private fun setCurrentUserPreferences(user: User) {
        for ((petrolID, radioBtn) in mapOfPetrolTypeRadioBtns) {
            if (petrolID == user.prefPetrol) {
                radioBtn.isChecked = true
                selectedPetrolTextView.text =
                        String.format(getString(R.string.preferred_petrol_string, radioBtn.text))
            }
        }

        for ((brandID, checkBox) in mapOfBrandCheckBoxes) {
            if (user.prefBrands.contains(brandID)) {
                checkBox.isChecked = true
            }
        }
    }

    private fun updateUserPreferences(user: User) {
        if (isPreferenceValid()) {
            AlertDialog.Builder(context!!)
                    .setIcon(R.drawable.settings)
                    .setTitle(R.string.change_preferences_title_string)
                    .setMessage(String.format(getString(R.string.confirm_change_preferences_string),
                            selectedPetrolTextView.text, produceStringOfPreferredStationBrands()))
                    .setPositiveButton(R.string.yes_string,
                            { dialog, which ->
                                user.prefPetrol = getPreferredPetrolType()
                                user.prefBrands = getPreferredStationBrandsList()

                                val trackNearbyIntent = Intent(context, TrackNearActivity::class.java)
                                startActivity(trackNearbyIntent)
                            })
                    .setNegativeButton(R.string.no_string, null)
                    .show()
        }
    }

    private fun isPreferenceValid(): Boolean {
        if(petrolTypesRadioGroup.checkedRadioButtonId == -1) {
            messageTextView.text = getString(R.string.invalid_preferences_string)

            return false
        } else {
            return true
        }
    }

    private fun produceStringOfPreferredStationBrands(): String {
        var stringOfPreferredStationBrands = ""

        for ((brandID, checkBox) in mapOfBrandCheckBoxes) {
            if (checkBox.isChecked) {
                stringOfPreferredStationBrands += (checkBox.text.toString() + "\n")
            }
        }
        return stringOfPreferredStationBrands
    }

    private fun getPreferredPetrolType(): String {
        var prefPetrol : String = String()

        for ((petrolID, radioBtn) in mapOfPetrolTypeRadioBtns) {
            if (radioBtn.isChecked) {
                prefPetrol = petrolID
            }
        }

        return prefPetrol
    }

    private fun getPreferredStationBrandsList(): ArrayList<String> {
        var preferredStationBrandsList : ArrayList<String> = ArrayList()

        for ((brandID, checkBox) in mapOfBrandCheckBoxes) {
            if (checkBox.isChecked) {
                preferredStationBrandsList.add(brandID)
            }
        }

        return preferredStationBrandsList
    }
}