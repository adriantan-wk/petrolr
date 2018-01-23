package com.example.apptivitylab.demoapp

import android.content.Context
import android.renderscript.ScriptGroup
import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.Petrol
import com.example.apptivitylab.demoapp.models.Station
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by ApptivityLab on 16/01/2018.
 */

class MockDataLoader {
//TODO Delete this class when actual data is available

    companion object {
        fun loadJSONStations (context: Context) : ArrayList<Station> {
            val stationList : ArrayList<Station> = ArrayList()
            val inputStream : InputStream = context.resources.openRawResource(R.raw.stations)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject : JSONObject
            var station : Station

            val fileContent = reader.readText()
            jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
            val jsonArray : JSONArray = jsonObject.optJSONArray("stations")

            for (s in 0 until jsonArray.length()) {
                station = Station(jsonArray.getJSONObject(s))
                stationList.add(station)
            }
            return stationList
        }

        fun loadJSONPetrolTypes (context: Context) : ArrayList<Petrol> {
            val petrolTypeList : ArrayList<Petrol> = ArrayList()
            val inputStream : InputStream = context.resources.openRawResource(R.raw.petroltypes)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject : JSONObject
            var petrolType : Petrol

            val fileContent = reader.readText()
            jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
            val jsonArray : JSONArray = jsonObject.optJSONArray("petrol_types")

            for (pt in 0 until jsonArray.length()) {
                petrolType = Petrol(jsonArray.getJSONObject(pt))
                petrolTypeList.add(petrolType)
            }
            return petrolTypeList
        }

        fun loadJSONBrands (context: Context) : ArrayList<Brand> {
            val brandList : ArrayList<Brand> = ArrayList()
            val inputStream : InputStream = context.resources.openRawResource(R.raw.brands)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var jsonObject : JSONObject
            var brand : Brand

            val fileContent = reader.readText()
            jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
            val jsonArray : JSONArray = jsonObject.optJSONArray("brands")

            for (pt in 0 until jsonArray.length()) {
                brand = Brand(jsonArray.getJSONObject(pt))
                brandList.add(brand)
            }
            return brandList
        }
    }

}