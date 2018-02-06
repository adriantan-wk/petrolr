package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import com.example.apptivitylab.demoapp.R
import org.json.JSONObject

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class Brand() : Parcelable {
    var brandID: String? = null
    var brandName: String? = null
    var brandLogo: Int = 0

    constructor(parcel: Parcel) : this() {
        this.brandID = parcel.readString()
        this.brandName = parcel.readString()
        this.brandLogo = parcel.readInt()
    }

    constructor(jsonObject: JSONObject) : this() {
        this.brandID = jsonObject.optString("brand_id")
        this.brandName = jsonObject.optString("brand_name")

        this.brandLogo = when (this.brandID) {
            "SHEL" -> R.drawable.ic_shell
            "BRPE" -> R.drawable.ic_bp
            "PTNS" -> R.drawable.ic_petronas
            "PTRN" -> R.drawable.ic_petron
            else -> 0
        }
    }

    constructor(brandID: String, brandName: String, brandLogo: Int) : this() {
        this.brandID = brandID
        this.brandName = brandName
        this.brandLogo = brandLogo
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.brandID)
        parcel.writeString(this.brandName)
        parcel.writeInt(this.brandLogo)
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