package com.example.apptivitylab.demoapp.models

import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class PetrolType() {
    var petrolID : String? = null
    var petrolName : String? = null
    var curPrice : Double? = null
    var prevPrices : ArrayList<Double> = ArrayList()
    var priceChangeDates : ArrayList<Date> = ArrayList()

    constructor(jsonObject: JSONObject) : this() {
        petrolID = jsonObject.optString("petrol_id")
        petrolName = jsonObject.optString("petrol_name")
        curPrice = jsonObject.optDouble("petrol_price")
    }
}