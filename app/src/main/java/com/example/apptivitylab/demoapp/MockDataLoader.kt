package com.example.apptivitylab.demoapp

import android.content.Context
import com.example.apptivitylab.demoapp.models.Station
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class MockDataLoader {
//TODO Delete this class when actual data is available

    companion object {
        fun loadStations (context: Context) : ArrayList<Station> {
            val listOfStations: ArrayList<Station> = ArrayList()

            val inputStream : InputStream = context.resources.openRawResource(R.raw.stations)
            val reader : BufferedReader = BufferedReader(InputStreamReader(inputStream))

            reader.forEachLine {
                val tokens : List<String> = it.split(";")

                val stationID = tokens[0]
                val stationName = tokens[1]
                val stationBrand = tokens[2]
                val stationLatLng = LatLng(tokens[3].toDouble(), tokens[4].toDouble())

                val station : Station = Station(stationID, stationName, stationBrand, stationLatLng)
                listOfStations.add(station)
            }

            return listOfStations
        }
    }

}