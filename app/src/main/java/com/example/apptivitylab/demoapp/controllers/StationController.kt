package com.example.apptivitylab.demoapp.controllers

import android.content.Context
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
    var stationList: ArrayList<Station> = ArrayList()

    fun loadStations(context: Context, onFullDataReceivedListener: RestAPIClient.OnFullDataReceivedListener) {
        val path = "data/stations?related=petrols_by_station_petrols"
        this.stationList.clear()

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

                            onFullDataReceivedListener.onFullDataReceived(true, null)
                        } else {
                            onFullDataReceivedListener.onFullDataReceived(false, error)
                        }
                    }
                })
    }
}