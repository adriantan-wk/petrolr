package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class PetrolType() : Parcelable {
    var petrolID: String? = null
    var petrolName: String? = null
    var currentPrice: Double? = null
    var previousPrices: ArrayList<Double> = ArrayList()
    var priceChangeDates: ArrayList<Date> = ArrayList()

    constructor(parcel: Parcel) : this() {
        petrolID = parcel.readString()
        petrolName = parcel.readString()
        currentPrice = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    constructor(jsonObject: JSONObject) : this() {
        petrolID = jsonObject.optString("petrol_id")
        petrolName = jsonObject.optString("petrol_name")
        currentPrice = jsonObject.optDouble("petrol_price")
    }

    constructor(petrolID: String, petrolName: String, currentPrice: Double) : this() {
        this.petrolID = petrolID
        this.petrolName = petrolName
        this.currentPrice = currentPrice
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(petrolID)
        parcel.writeString(petrolName)
        parcel.writeValue(currentPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PetrolType> {
        override fun createFromParcel(parcel: Parcel): PetrolType {
            return PetrolType(parcel)
        }

        override fun newArray(size: Int): Array<PetrolType?> {
            return arrayOfNulls(size)
        }
    }
}