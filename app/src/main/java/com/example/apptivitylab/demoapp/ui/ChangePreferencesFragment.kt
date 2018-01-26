package com.example.apptivitylab.demoapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import com.example.apptivitylab.demoapp.MockDataLoader
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.R.id.*
import com.example.apptivitylab.demoapp.controllers.UserController
import com.example.apptivitylab.demoapp.controllers.UserController.user
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.Station
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.fragment_change_preferences.*

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class ChangePreferencesFragment : Fragment() {

    companion object {
        const val USER_EXTRA = "user_object"

        fun newInstance(currentUser: User): ChangePreferencesFragment {
            val fragment = ChangePreferencesFragment()

            val args: Bundle = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)

            fragment.arguments = args
            return fragment
        }
    }

    private var petrolTypes: ArrayList<PetrolType> = ArrayList()
    private var brands: ArrayList<Brand> = ArrayList()

    private var radioButtonsByPetrolType: HashMap<PetrolType, RadioButton> = HashMap()
    private var checkBoxesByBrand: HashMap<Brand, CheckBox> = HashMap()

    private lateinit var currentUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
        }

        this.petrolTypes = MockDataLoader.loadJSONPetrolTypes(context!!)
        this.brands = MockDataLoader.loadJSONBrands(context!!)

        this.selectedPetrolTextView.text = String.format(getString(R.string.preferred_petrol_string, ""))

        this.createPetrolRadioButtons(this.petrolTypes, this.radioButtonsByPetrolType)
        this.createBrandCheckBoxes(this.brands, this.checkBoxesByBrand)

        this.presetCurrentUserPreferences(this.currentUser, this.radioButtonsByPetrolType, this.checkBoxesByBrand)

        this.saveBtn.setOnClickListener {
            this.updateUserPreferences(this.currentUser)
        }

        this.selectAllButton.setOnClickListener {
            for (checkBox in this.checkBoxesByBrand.values) {
                checkBox.isChecked = true
            }
        }
    }

    private fun createPetrolRadioButtons(petrolTypes: ArrayList<PetrolType>, radioButtonsByPetrolType: HashMap<PetrolType, RadioButton>) {
        petrolTypes.forEach { petrolType ->
            val radioButton = RadioButton(context)
            radioButton.text = petrolType.petrolName

            radioButton.setOnClickListener {
                selectedPetrolTextView.text = String.format(getString(R.string.preferred_petrol_string, radioButton.text))
            }
            this.petrolTypesRadioGroup.addView(radioButton)

            radioButtonsByPetrolType.put(petrolType, radioButton)
        }
    }

    private fun createBrandCheckBoxes(brands: ArrayList<Brand>, checkBoxesByBrand: HashMap<Brand, CheckBox>) {
        brands.forEach { brand ->
            val checkBox = CheckBox(context)
            checkBox.text = brand.brandName
            this.brandsLinearLayout.addView(checkBox)

            checkBoxesByBrand.put(brand, checkBox)
        }
    }

    private fun presetCurrentUserPreferences(currentUser: User,
                                             radioButtonsByPetrolType: HashMap<PetrolType, RadioButton>,
                                             checkBoxesByBrand: HashMap<Brand, CheckBox>) {
        for ((petrolType, radioButton) in radioButtonsByPetrolType) {
            if (petrolType.petrolID == currentUser.preferredPetrolType?.petrolID) {
                radioButton.isChecked = true
                selectedPetrolTextView.text =
                        String.format(getString(R.string.preferred_petrol_string, radioButton.text))
            }
        }

        for ((brand, checkBox) in checkBoxesByBrand) {
            user.preferredBrands
                    .filter { brand.brandID == it.brandID }
                    .forEach { checkBox.isChecked = true }
        }
    }

    private fun updateUserPreferences(currentUser: User) {
        if (isPreferenceValid()) {
            AlertDialog.Builder(context!!)
                    .setIcon(R.drawable.ic_settings)
                    .setTitle(R.string.change_preferences_title_string)
                    .setMessage(String.format(getString(R.string.confirm_change_preferences_string),
                            selectedPetrolTextView.text, produceStringOfPreferredStationBrands(this.checkBoxesByBrand)))
                    .setPositiveButton(R.string.yes_string,
                            { dialog, which ->
                                currentUser.preferredPetrolType = getPreferredPetrolType(this.radioButtonsByPetrolType)
                                currentUser.preferredBrands = getPreferredStationBrandsList(this.checkBoxesByBrand)

                                val intent = Intent()
                                intent.putExtra(getString(R.string.change_preferences_intent_string), currentUser)

                                activity?.setResult(Activity.RESULT_OK, intent)
                                activity?.finish()
                            })
                    .setNegativeButton(R.string.no_string, null)
                    .show()
        }
    }

    private fun isPreferenceValid(): Boolean {
        val isRadioButtonInvalid = petrolTypesRadioGroup.checkedRadioButtonId == -1
        val isCheckBoxInvalid = !checkBoxesByBrand.values.any { it.isChecked }

        return if (isRadioButtonInvalid || isCheckBoxInvalid) {
            messageTextView.text = getString(R.string.invalid_preferences_string)
            false
        } else {
            true
        }
    }

    private fun produceStringOfPreferredStationBrands(checkBoxesByBrand: HashMap<Brand, CheckBox>): String {
        var stringOfPreferredStationBrands = ""

        for ((brand, checkBox) in checkBoxesByBrand) {
            if (checkBox.isChecked) {
                stringOfPreferredStationBrands += (checkBox.text.toString() + "\n")
            }
        }

        return stringOfPreferredStationBrands
    }

    private fun getPreferredPetrolType(radioButtonsByPetrolType: HashMap<PetrolType, RadioButton>): PetrolType {
        var preferredPetrol: PetrolType = PetrolType()

        for ((petrolType, radioButton) in radioButtonsByPetrolType) {
            if (radioButton.isChecked) {
                preferredPetrol = petrolType
            }
        }

        return preferredPetrol
    }

    private fun getPreferredStationBrandsList(checkBoxesByBrand: HashMap<Brand, CheckBox>): ArrayList<Brand> {
        var preferredStationBrandsList: ArrayList<Brand> = ArrayList()

        for ((brand, checkBox) in checkBoxesByBrand) {
            if (checkBox.isChecked) {
                preferredStationBrandsList.add(brand)
            }
        }

        return preferredStationBrandsList
    }
}