package com.example.apptivitylab.demoapp.controllers

import android.content.Context
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.PetrolType
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by ApptivityLab on 25/01/2018.
 */

object PetrolTypeController {
    lateinit var petrolTypeList: ArrayList<PetrolType>

    fun loadMockPetrolTypes(context: Context) {
        val petrolTypeList: ArrayList<PetrolType> = ArrayList()
        val inputStream: InputStream = context.resources.openRawResource(R.raw.petroltypes)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var jsonObject: JSONObject
        var petrolType: PetrolType

        val fileContent = reader.readText()
        jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
        val jsonArray: JSONArray = jsonObject.optJSONArray("petrol_types")

        for (pt in 0 until jsonArray.length()) {
            petrolType = PetrolType(jsonArray.getJSONObject(pt))
            petrolTypeList.add(petrolType)
        }

        this.petrolTypeList = petrolTypeList
    }
}