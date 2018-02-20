package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import com.example.apptivitylab.demoapp.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class Brand() : Parcelable {
    var brandID: String? = null
    var brandName: String? = null
    var brandUpdatedAt: Date? = null
    var brandCreatedAt: Date? = null
    var brandLogo: Int = 0
    var brandWebsite: String? = null

    constructor(parcel: Parcel) : this() {
        this.brandID = parcel.readString()
        this.brandName = parcel.readString()
        this.brandUpdatedAt = Date(parcel.readLong())
        this.brandCreatedAt = Date(parcel.readLong())
        this.brandLogo = parcel.readInt()
        this.brandWebsite = parcel.readString()
    }

    constructor(jsonObject: JSONObject) : this() {
        this.brandID = jsonObject.optString("uuid")
        this.brandName = jsonObject.optString("name")

        this.brandUpdatedAt = SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("updated_at"))
        this.brandCreatedAt = SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("created_at"))

        this.brandLogo = when (this.brandName) {
            "Shell" -> R.drawable.ic_shell
            "BHPetrol" -> R.drawable.ic_bp
            "Petronas" -> R.drawable.ic_petronas
            "Petron" -> R.drawable.ic_petron
            "Caltex" -> R.drawable.ic_caltex
            else -> 0
        }

        this.brandWebsite = jsonObject.optString("website")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.brandID)
        parcel.writeString(this.brandName)

        this.brandUpdatedAt?.let {
            parcel.writeLong(it.time)
        }

        this.brandCreatedAt?.let {
            parcel.writeLong(it.time)
        }

        parcel.writeInt(this.brandLogo)
        parcel.writeString(this.brandWebsite)
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