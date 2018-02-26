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
    var petrolTypeList: ArrayList<PetrolType> = ArrayList()

    fun loadPetrolTypes(context: Context, onFullDataReceivedListener: RestAPIClient.OnFullDataReceivedListener) {
        val path = "data/petrols?related=price_histories_by_petrol_uuid"
        this.petrolTypeList.clear()

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