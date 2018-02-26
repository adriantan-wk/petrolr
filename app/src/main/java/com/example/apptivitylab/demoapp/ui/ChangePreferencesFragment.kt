package com.example.apptivitylab.demoapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.User
import kotlinx.android.synthetic.main.cell_brand.view.*
import kotlinx.android.synthetic.main.dialog_change_preferred_brands.view.*
import kotlinx.android.synthetic.main.dialog_change_preferred_petrol.view.*
import kotlinx.android.synthetic.main.fragment_change_preferences.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class ChangePreferencesFragment : Fragment(), RestAPIClient.OnFullDataReceivedListener {

    companion object {
        const val NO_OF_RESOURCE_SETS = 2

        const val USER_EXTRA = "user_object"
        const val NEW_USER_BOOLEAN_EXTRA = "new_user_boolean"

        fun newInstance(currentUser: User, isNewUser: Boolean): ChangePreferencesFragment {
            val fragment = ChangePreferencesFragment()

            val args: Bundle = Bundle()
            args.putParcelable(USER_EXTRA, currentUser)
            args.putBoolean(NEW_USER_BOOLEAN_EXTRA, isNewUser)

            fragment.arguments = args
            return fragment
        }
    }

    private var dataResourcesReceived = 0

    private lateinit var changePreferencesActivity: ChangePreferencesActivity

    private var petrolTypes: ArrayList<PetrolType> = ArrayList()
    private var brands: ArrayList<Brand> = ArrayList()

    private var preferredPetrolType: PetrolType? = null
    private var preferredBrands: ArrayList<Brand> = ArrayList()

    private lateinit var currentUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_preferences, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isNewUser = false
        changePreferencesActivity = this.activity as ChangePreferencesActivity

        arguments?.let {
            this.currentUser = it.getParcelable(USER_EXTRA)
            isNewUser = it.getBoolean(NEW_USER_BOOLEAN_EXTRA)
        }

        if (isNewUser) {
            this.loadAppData()
        } else {
            this.performFragmentStartup()
        }
    }

    private fun loadAppData() {
        this.progressBar.visibility = View.VISIBLE
        this.progressBarTextView.visibility = View.VISIBLE

        BrandController.loadBrands(this.context!!, this)
        PetrolTypeController.loadPetrolTypes(this.context!!, this)
    }

    override fun onFullDataReceived(dataReceived: Boolean, error: VolleyError?) {
        if (!dataReceived || error != null) {
            view?.let {
                this.dataResourcesReceived = 0
                this.progressBar.visibility = View.GONE
                this.progressBarTextView.visibility = View.GONE

                Snackbar.make(it, getString(R.string.failed_retrieve_data), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.retry), View.OnClickListener {
                            this.loadAppData()
                        })
                        .show()
            }
        } else {
            this.dataResourcesReceived++

            if (dataResourcesReceived == NO_OF_RESOURCE_SETS) {
                this.performFragmentStartup()
            }
        }
    }

    private fun performFragmentStartup() {
        this.petrolTypes = PetrolTypeController.petrolTypeList
        this.brands = BrandController.brandList

        this.progressBar.visibility = View.GONE
        this.progressBarTextView.visibility = View.GONE

        this.changePreferredPetrolButton.setOnClickListener {
            this.displayChangePreferredPetrolDialog()
        }

        this.changePreferredBrandsButton.setOnClickListener {
            this.displayChangePreferredBrandsDialog()
        }

        this.presetCurrentUserPreferences(this.currentUser)
    }

    private fun displayChangePreferredPetrolDialog() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_preferred_petrol, null)

        this.createPetrolRadioButtons(this.petrolTypes, dialogView.petrolTypesRadioGroup)
        this.presetUserPetrolPreferences(dialogView.petrolTypesRadioGroup, this.preferredPetrolType)

        val alertDialog = AlertDialog.Builder(this.context!!)
                .setView(dialogView)
                .setPositiveButton(R.string.ok,
                        { dialog, which ->
                            if (this.isPreferenceValid(getString(R.string.preferred_petrol), dialogView.petrolTypesRadioGroup)) {
                                val selectedRadioButton = dialogView.findViewById<RadioButton>(dialogView.petrolTypesRadioGroup.checkedRadioButtonId)

                                this.preferredPetrolType = this.petrolTypes.firstOrNull { petrolType ->
                                    petrolType.petrolName == selectedRadioButton.text
                                }

                                this.preferredPetrolTextView.text = this.preferredPetrolType?.petrolName

                                this.changePreferencesActivity.unsavedChangesMade = true
                            } else {
                                this.showFailedChangeMessage(getString(R.string.preferred_petrol))
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun createPetrolRadioButtons(petrolTypes: ArrayList<PetrolType>, radioGroup: RadioGroup) {
        petrolTypes.forEach { petrolType ->
            val radioButton = RadioButton(context)
            radioButton.text = petrolType.petrolName

            radioGroup.addView(radioButton)
        }
    }

    private fun presetUserPetrolPreferences(radioGroup: RadioGroup, preferredPetrolType: PetrolType?) {
        if (preferredPetrolType != null) {
            (0 until radioGroup.childCount).forEach { childCount ->
                val radioButton = radioGroup.getChildAt(childCount) as RadioButton

                if (radioButton.text == preferredPetrolType.petrolName) {
                    radioButton.isChecked = true
                }
            }
        }
    }

    private fun displayChangePreferredBrandsDialog() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_preferred_brands, null)

        this.createBrandCheckBoxes(this.brands, dialogView.brandCheckBoxesLinearLayout)
        this.presetUserBrandPreferences(dialogView.brandCheckBoxesLinearLayout, this.preferredBrands)

        dialogView.selectAllButton.setOnClickListener {
            (0 until dialogView.brandCheckBoxesLinearLayout.childCount).forEach { child ->
                val checkBox = dialogView.brandCheckBoxesLinearLayout.getChildAt(child) as CheckBox
                checkBox.isChecked = true
            }
        }

        val alertDialog = AlertDialog.Builder(this.context!!)
                .setView(dialogView)
                .setPositiveButton(R.string.ok,
                        { dialog, which ->
                            if (this.isPreferenceValid(getString(R.string.preferred_station_brands), dialogView.brandCheckBoxesLinearLayout)) {
                                this.preferredBrands.clear()

                                val listOfCheckedBrandNames = (0 until dialogView.brandCheckBoxesLinearLayout.childCount)
                                        .map { dialogView.brandCheckBoxesLinearLayout.getChildAt(it) as CheckBox }
                                        .filter { it.isChecked }
                                        .map { it.text }

                                this.preferredBrands.addAll(this.brands
                                        .filter { listOfCheckedBrandNames.contains(it.brandName) })

                                this.generatePreferredBrandsList(this.preferredBrands, this.brandsLinearLayout)
                                this.changePreferencesActivity.unsavedChangesMade = true
                            } else {
                                this.showFailedChangeMessage(getString(R.string.preferred_station_brands))
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun createBrandCheckBoxes(brands: ArrayList<Brand>, brandCheckBoxesLinearLayout: LinearLayout) {
        brands.forEach { brand ->
            val checkBox = CheckBox(context)
            checkBox.text = brand.brandName
            brandCheckBoxesLinearLayout.addView(checkBox)
        }
    }

    private fun presetUserBrandPreferences(brandCheckBoxesLinearLayout: LinearLayout, preferredBrands: ArrayList<Brand>) {
        if (preferredBrands.isNotEmpty()) {
            (0 until brandCheckBoxesLinearLayout.childCount)
                    .map { brandCheckBoxesLinearLayout.getChildAt(it) as CheckBox }
                    .forEach {
                        if (preferredBrands.any { brand ->
                                    it.text == brand.brandName
                                }) {
                            it.isChecked = true
                        }
                    }
        }
    }

    private fun presetCurrentUserPreferences(currentUser: User) {
        if (currentUser.preferredPetrolType != null && currentUser.preferredBrands.isNotEmpty()) {
            this.preferredPetrolType = currentUser.preferredPetrolType
            preferredPetrolTextView.text = currentUser.preferredPetrolType?.petrolName

            this.generatePreferredBrandsList(this.currentUser.preferredBrands, this.brandsLinearLayout)
        } else {
            this.preferredPetrolTextView.text = getString(R.string.none)
        }
    }

    private fun generatePreferredBrandsList(brands: ArrayList<Brand>, brandsLinearLayout: LinearLayout) {
        brandsLinearLayout.removeAllViews()
        val preferredBrands: ArrayList<Brand> = ArrayList()

        brands.forEach { brand ->
            val layoutInflater: LayoutInflater = LayoutInflater.from(this.context)
            val brandCell = layoutInflater.inflate(R.layout.cell_brand, brandsLinearLayout, false)

            brandCell.logoImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, brand.brandLogo, null))
            brandCell.brandTextView.text = brand.brandName
            brandsLinearLayout.addView(brandCell)
            preferredBrands.add(brand)
        }

        if (this.preferredBrands.isEmpty()) {
            this.preferredBrands.addAll(preferredBrands)
        }
    }

    fun updateUserPreferences(currentUser: User) {
        if (this.preferredPetrolType != null && this.preferredBrands.isNotEmpty()) {
            AlertDialog.Builder(context!!)
                    .setIcon(R.drawable.ic_settings)
                    .setTitle(R.string.change_preferences_title)
                    .setMessage(String.format(getString(R.string.confirm_change_preferences),
                            preferredPetrolTextView.text, produceStringOfPreferredStationBrands(this.preferredBrands)))
                    .setPositiveButton(R.string.yes,
                            { dialog, which ->
                                currentUser.preferredPetrolType = this.preferredPetrolType
                                currentUser.preferredBrands = this.preferredBrands

                                this.preferredPetrolType?.let {
                                    this.writePreferencesToFile(currentUser, it, this.preferredBrands)
                                }

                                this.changePreferencesActivity.unsavedChangesMade = false

                                val intent = Intent()
                                intent.putExtra(getString(R.string.change_preferences_intent), currentUser)

                                activity?.setResult(Activity.RESULT_OK, intent)
                                activity?.finish()
                            })
                    .setNegativeButton(R.string.no, null)
                    .show()
        } else {
            AlertDialog.Builder(context!!)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.invalid_preferences_message)
                    .setPositiveButton(R.string.ok, null)
                    .show()
        }
    }

    private fun writePreferencesToFile(currentUser: User, preferredPetrolType: PetrolType, preferredBrands: ArrayList<Brand>) {
        val fileName = currentUser.username + ".json"
        var jsonObject = JSONObject()
        jsonObject.put("preferred_petrol_type", preferredPetrolType.petrolID)

        val jsonArray = JSONArray()
        preferredBrands.forEach { brand ->
            jsonArray.put(brand.brandID)
        }
        jsonObject.put("preferred_brands", jsonArray)

        val file = OutputStreamWriter(this.context!!.applicationContext.openFileOutput(fileName, Activity.MODE_PRIVATE))
        file.write(jsonObject.toString())
        file.flush()
        file.close()
    }

    private fun isPreferenceValid(petrolOrBrands: String, viewGroup: View): Boolean {
        if (petrolOrBrands == getString(R.string.preferred_petrol)) {
            val radioGroup = viewGroup as RadioGroup

            return radioGroup.checkedRadioButtonId != -1
        } else {
            val linearLayout = viewGroup as LinearLayout

            (0 until linearLayout.childCount).forEach { child ->
                val checkBox = linearLayout.getChildAt(child) as CheckBox

                if (checkBox.isChecked)
                    return true
            }
            return false
        }
    }

    private fun produceStringOfPreferredStationBrands(preferredBrands: ArrayList<Brand>): String {
        return preferredBrands
                .map { it.brandName }
                .joinToString("\n")
    }

    private fun showFailedChangeMessage(petrolOrBrands: String) {
        var message = if (petrolOrBrands == getString(R.string.preferred_petrol)) {
            getString(R.string.change_preference_failed, getString(R.string.invalid_preferences_petrol))
        } else {
            getString(R.string.change_preference_failed, getString(R.string.invalid_preferences_brand))
        }

        AlertDialog.Builder(context!!)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show()
    }
}