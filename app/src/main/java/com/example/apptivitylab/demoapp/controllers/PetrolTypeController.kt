package com.example.apptivitylab.demoapp.controllers

import android.content.Context
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
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

    fun loadPetrolTypes(context: Context, onFullDataReceivedListener: RestAPIClient.OnFullDataReceivedListener) {
        val path = "data/petrols?related=price_histories_by_petrol_uuid"

        RestAPIClient.shared(context).getResources(path, null,
                object : RestAPIClient.OnGetResourceCompletedListener {
                    override fun onComplete(jsonObject: JSONObject?, error: VolleyError?) {
                        if (jsonObject != null) {
                            var petrolTypeList: ArrayList<PetrolType> = ArrayList()
                            var petrolType: PetrolType

                            val jsonArray: JSONArray = jsonObject.optJSONArray("resource")

                            for (item in 0 until jsonArray.length()) {
                                petrolType = PetrolType(jsonArray.getJSONObject(item))

                                petrolTypeList.add(petrolType)
                            }
                            this@PetrolTypeController.petrolTypeList = petrolTypeList

                            onFullDataReceivedListener.onFullDataReceived(true, null)
                        } else {
                            onFullDataReceivedListener.onFullDataReceived(false, error)
                        }
                    }
                })
    }
}