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

    private var listOfPetrolTypes: ArrayList<Petrol> = ArrayList()
    private var listOfBrands: ArrayList<Brand> = ArrayList()

    private var radioButtonsByPetrolType: HashMap<String, RadioButton> = HashMap()
    private var checkBoxesByBrand: HashMap<String, CheckBox> = HashMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.listOfPetrolTypes = MockDataLoader.loadJSONPetrolTypes(context!!)
        this.listOfBrands = MockDataLoader.loadJSONBrands(context!!)

        this.selectedPetrolTextView.text = String.format(getString(R.string.preferred_petrol_string, ""))

        this.createPetrolRadioButtons(this.listOfPetrolTypes, this.radioButtonsByPetrolType)
        this.createBrandCheckBoxes(this.listOfBrands, this.checkBoxesByBrand)

        this.presetCurrentUserPreferences(UserController.user, this.radioButtonsByPetrolType, this.checkBoxesByBrand)

        this.saveBtn.setOnClickListener {
            this.updateUserPreferences(UserController.user)
        }
    }

    private fun createPetrolRadioButtons(listOfPetrolTypes: ArrayList<Petrol>, radioButtonsByPetrolType: HashMap<String, RadioButton>) {
        listOfPetrolTypes.forEach { petrol ->
            val radioButton = RadioButton(context)
            radioButton.text = petrol.petrolName

            radioButton.setOnClickListener {
                selectedPetrolTextView.text = String.format(getString(R.string.preferred_petrol_string, radioButton.text))
            }
            this.petrolTypesRadioGroup.addView(radioButton)

            petrol.petrolID?.let {
                radioButtonsByPetrolType.put(it, radioButton)
            }
        }
    }

    private fun createBrandCheckBoxes(listOfBrands: ArrayList<Brand>, checkBoxesByBrand: HashMap<String, CheckBox>) {
        listOfBrands.forEach { brand ->
            val checkBox = CheckBox(context)
            checkBox.text = brand.brandName
            this.brandsLinearLayout.addView(checkBox)

            brand.brandID?.let {
                checkBoxesByBrand.put(it, checkBox)
            }
        }
    }

    private fun presetCurrentUserPreferences(user: User,
                                             radioButtonsByPetrolType: HashMap<String, RadioButton>,
                                             checkBoxesByBrand: HashMap<String, CheckBox>) {
        for ((petrolID, radioButton) in radioButtonsByPetrolType) {
            if (petrolID == user.prefPetrol) {
                radioButton.isChecked = true
                selectedPetrolTextView.text =
                        String.format(getString(R.string.preferred_petrol_string, radioButton.text))
            }
        }

        for ((brandID, checkBox) in checkBoxesByBrand) {
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
                            selectedPetrolTextView.text, produceStringOfPreferredStationBrands(this.checkBoxesByBrand)))
                    .setPositiveButton(R.string.yes_string,
                            { dialog, which ->
                                user.prefPetrol = getPreferredPetrolType(this.radioButtonsByPetrolType)
                                user.prefBrands = getPreferredStationBrandsList(this.checkBoxesByBrand)

                                val trackNearbyIntent = Intent(context, TrackNearActivity::class.java)
                                startActivity(trackNearbyIntent)
                            })
                    .setNegativeButton(R.string.no_string, null)
                    .show()
        }
    }

    private fun isPreferenceValid(): Boolean {
        val isRadioButtonInvalid = petrolTypesRadioGroup.checkedRadioButtonId == -1

        return if (isRadioButtonInvalid) {
            messageTextView.text = getString(R.string.invalid_preferences_string)
            false
        } else {
            true
        }
    }

    private fun produceStringOfPreferredStationBrands(checkBoxesByBrand: HashMap<String, CheckBox>): String {
        var stringOfPreferredStationBrands = ""

        for ((brandID, checkBox) in checkBoxesByBrand) {
            if (checkBox.isChecked) {
                stringOfPreferredStationBrands += (checkBox.text.toString() + "\n")
            }
        }
        return stringOfPreferredStationBrands
    }

    private fun getPreferredPetrolType(radioButtonsByPetrolType: HashMap<String, RadioButton>): String {
        var preferredPetrol: String = String()

        for ((petrolID, radioButton) in radioButtonsByPetrolType) {
            if (radioButton.isChecked) {
                preferredPetrol = petrolID
            }
        }

        return preferredPetrol
    }

    private fun getPreferredStationBrandsList(checkBoxesByBrand: HashMap<String, CheckBox>): ArrayList<String> {
        var preferredStationBrandsList: ArrayList<String> = ArrayList()

        for ((brandID, checkBox) in checkBoxesByBrand) {
            if (checkBox.isChecked) {
                preferredStationBrandsList.add(brandID)
            }
        }

        return preferredStationBrandsList
    }
}