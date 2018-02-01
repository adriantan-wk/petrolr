package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class PetrolType() : Parcelable {
    var petrolID: String? = null
    var petrolName: String? = null
    var currentPrice: Double = 0.0
    var previousPrices: ArrayList<Double> = ArrayList()
    var priceChangeDates: ArrayList<Date> = ArrayList()

    constructor(parcel: Parcel) : this() {
        this.petrolID = parcel.readString()
        this.petrolName = parcel.readString()
        this.currentPrice = parcel.readDouble()
        this.previousPrices = parcel.readArrayList(Double::class.java.classLoader) as ArrayList<Double>
        this.priceChangeDates = parcel.readArrayList(Date::class.java.classLoader) as ArrayList<Date>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.petrolID = jsonObject.optString("petrol_id")
        this.petrolName = jsonObject.optString("petrol_name")

        val petrolPricesJsonArray = jsonObject.optJSONArray("petrol_previous_prices")
        for (previousPrice in 0 until petrolPricesJsonArray.length()) {
            this.previousPrices.add(petrolPricesJsonArray.getDouble(previousPrice))
        }

        val petrolPriceChangeDatesJsonArray = jsonObject.optJSONArray("petrol_price_change_dates")
        for (changeDate in 0 until petrolPriceChangeDatesJsonArray.length()) {
            val dateString = petrolPriceChangeDatesJsonArray.getString(changeDate)
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

            this.priceChangeDates.add(dateFormatter.parse(dateString))
        }

        this.currentPrice = this.previousPrices[0]
    }

    constructor(petrolID: String, petrolName: String, currentPrice: Double) : this() {
        this.petrolID = petrolID
        this.petrolName = petrolName
        this.currentPrice = currentPrice
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.petrolID)
        parcel.writeString(this.petrolName)
        parcel.writeDouble(this.currentPrice)
        parcel.writeList(this.previousPrices)
        parcel.writeList(this.priceChangeDates)
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