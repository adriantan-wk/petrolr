package com.example.apptivitylab.demoapp.models

import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class PetrolType() {
    var petrolID: String? = null
    var petrolName: String? = null
    var currentPrice: Double? = null
    var previousPrices: ArrayList<Double> = ArrayList()
    var priceChangeDates: ArrayList<Date> = ArrayList()

    constructor(jsonObject: JSONObject): this() {
        petrolID = jsonObject.optString("petrol_id")
        petrolName = jsonObject.optString("petrol_name")
        currentPrice = jsonObject.optDouble("petrol_price")
    }

    constructor(petrolID: String, petrolName: String, currentPrice: Double): this() {
        this.petrolID = petrolID
        this.petrolName = petrolName
        this.currentPrice = currentPrice
    }
}