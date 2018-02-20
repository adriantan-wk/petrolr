package com.example.apptivitylab.demoapp.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
import com.example.apptivitylab.demoapp.models.Station
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by ApptivityLab on 25/01/2018.
 */

object StationController {
    lateinit var stationList: ArrayList<Station>

    fun loadMockStations(context: Context) {
        val stationList: ArrayList<Station> = ArrayList()
        val inputStream: InputStream = context.resources.openRawResource(R.raw.stations)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var jsonObject: JSONObject
        var station: Station

        val fileContent = reader.readText()
        jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
        val jsonArray: JSONArray = jsonObject.optJSONArray("stations")

        for (s in 0 until jsonArray.length()) {
            station = Station(jsonArray.getJSONObject(s))
            stationList.add(station)
        }

        this.stationList = stationList
    }

    fun loadStations(context: Context) {
        val path = "data/stations?related=petrols_by_station_petrols"

        RestAPIClient.shared(context).getResources(path, 100,
                object : RestAPIClient.OnGetResourceCompletedListener {
                    override fun onComplete(jsonObject: JSONObject?, error: VolleyError?) {
                        if (jsonObject != null) {
                            var stationList: ArrayList<Station> = ArrayList()
                            var station: Station

                            val jsonArray: JSONArray = jsonObject.optJSONArray("resource")

                            for (item in 0 until jsonArray.length()) {
                                station = Station(jsonArray.getJSONObject(item))

                                stationList.add(station)
                            }
                            this@StationController.stationList = stationList

                        } else {
                            Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }
}