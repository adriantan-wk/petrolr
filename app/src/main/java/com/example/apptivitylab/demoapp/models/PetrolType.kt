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
    var petrolCreatedAt: Date? = null
    var petrolUpdatedAt: Date? = null
    var currentPrice: Double = 0.0
    var previousPrices: ArrayList<Double> = ArrayList()
    var priceChangeDates: ArrayList<Date> = ArrayList()

    constructor(parcel: Parcel) : this() {
        this.petrolID = parcel.readString()
        this.petrolName = parcel.readString()
        this.petrolCreatedAt = Date(parcel.readLong())
        this.petrolUpdatedAt = Date(parcel.readLong())
        this.currentPrice = parcel.readDouble()
        this.previousPrices = parcel.readArrayList(Double::class.java.classLoader) as ArrayList<Double>
        this.priceChangeDates = parcel.readArrayList(Date::class.java.classLoader) as ArrayList<Date>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.petrolID = jsonObject.optString("uuid")
        this.petrolName = jsonObject.optString("name")

        this.petrolCreatedAt = (SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("created_at")))
        this.petrolUpdatedAt = (SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("updated_at")))

        val petrolPriceJSONArray = jsonObject.optJSONArray("price_histories_by_petrol_uuid")
        for (priceObject in 0 until petrolPriceJSONArray.length()) {
            this.previousPrices.add(petrolPriceJSONArray.getJSONObject(priceObject).optDouble("price_cents") / 100)
            this.priceChangeDates.add(SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(petrolPriceJSONArray.getJSONObject(priceObject).optString("updated_at")))
        }

        if (this.previousPrices.isEmpty()) {
            this.currentPrice = 0.0
        } else {
            this.currentPrice = this.previousPrices[0]
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.petrolID)
        parcel.writeString(this.petrolName)
        parcel.writeLong(this.petrolCreatedAt!!.time)
        parcel.writeLong(this.petrolUpdatedAt!!.time)
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