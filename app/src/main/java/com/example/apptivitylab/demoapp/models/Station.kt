package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class Station() : Parcelable {
    var stationCreatedAt: Date? = null
    var stationUpdatedAt: Date? = null
    var stationName: String? = null
    var stationID: String? = null
    var stationLatLng: LatLng? = null
    var stationBrand: String? = null
    var stationPetrolTypeIDs: ArrayList<String> = ArrayList()
    var distanceFromUser: Float? = null

    constructor(parcel: Parcel) : this() {
        this.stationCreatedAt = Date(parcel.readLong())
        this.stationUpdatedAt = Date(parcel.readLong())
        this.stationName = parcel.readString()
        this.stationID = parcel.readString()
        this.stationLatLng = parcel.readParcelable(LatLng::class.java.classLoader)
        this.stationBrand = parcel.readString()
        this.stationPetrolTypeIDs = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.stationCreatedAt = (SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("created_at")))
        this.stationUpdatedAt = (SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("updated_at")))

        this.stationName = jsonObject.optString("name")
        this.stationID = jsonObject.optString("uuid")
        this.stationLatLng = LatLng(jsonObject.optDouble("latitude"), jsonObject.optDouble("longitude"))
        this.stationBrand = jsonObject.optString("company_uuid")

        val petrolIDJsonArray = jsonObject.optJSONArray("petrols_by_station_petrols")
        for (petrolID in 0 until petrolIDJsonArray.length()) {
            this.stationPetrolTypeIDs.add(petrolIDJsonArray.getJSONObject(petrolID).optString("uuid"))
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        this.stationCreatedAt?.let {
            parcel.writeLong(it.time)
        }

        this.stationUpdatedAt?.let {
            parcel.writeLong(it.time)
        }

        parcel.writeString(this.stationName)
        parcel.writeString(this.stationID)
        parcel.writeParcelable(this.stationLatLng, flags)
        parcel.writeString(this.stationBrand)
        parcel.writeList(this.stationPetrolTypeIDs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }

}