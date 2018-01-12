package com.example.apptivitylab.demoapp.models

import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class Station {
    val stationID : Int
    val stationName : String
    val stationBrand : String
    //val stationPetrolTypes : Array<String>
    val stationLat : Double
    val stationLong : Double

    constructor(jsonObject: JSONObject) {
        stationID = jsonObject.optInt("ID:")
        stationName = jsonObject.optString("Name:")
        stationBrand = jsonObject.optString("Brand")
        //stationPetrolTypes = jsonObject.opt("Petrol")
        stationLat = jsonObject.optDouble("Latitude")
        stationLong = jsonObject.optDouble("Longitude")
    }
}