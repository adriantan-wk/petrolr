package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class Brand() : Parcelable {
    var brandID: String? = null
    var brandName: String? = null
    var stations: ArrayList<Station> = ArrayList()

    constructor(parcel: Parcel) : this() {
        brandID = parcel.readString()
        brandName = parcel.readString()
    }

    constructor(jsonObject: JSONObject) : this() {
        brandID = jsonObject.optString("brand_id")
        brandName = jsonObject.optString("brand_name")
    }

    constructor(brandID: String, brandName: String) : this() {
        this.brandID = brandID
        this.brandName = brandName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brandID)
        parcel.writeString(brandName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Brand> {
        override fun createFromParcel(parcel: Parcel): Brand {
            return Brand(parcel)
        }

        override fun newArray(size: Int): Array<Brand?> {
            return arrayOfNulls(size)
        }
    }
}