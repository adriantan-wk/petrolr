package com.example.apptivitylab.demoapp.models

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class Station() {
    //private val stationPetrolTypes : Array<String>
    var stationID : Int? = null
    var stationName : String? = null
    var stationBrand : String? = null
    var stationLatLng : LatLng? = null

    constructor(stationID : Int, stationName : String, stationBrand : String, stationLatLng : LatLng) : this() {
        this.stationID = stationID
        this.stationName = stationName
        this.stationBrand = stationBrand
        this.stationLatLng = stationLatLng
    }

    constructor(jsonObject: JSONObject) : this()
    {
        stationID = jsonObject.optInt("ID:")
        stationName = jsonObject.optString("Name:")
        stationBrand = jsonObject.optString("Brand")
        stationLatLng = LatLng(jsonObject.optDouble("Latitude"), jsonObject.optDouble("Longitude"))
        //stationPetrolTypes = jsonObject.opt("Petrol")
    }
}