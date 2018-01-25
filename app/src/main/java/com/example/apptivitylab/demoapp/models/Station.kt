package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class Station() : Parcelable{
    var stationID: String? = null
    var stationName: String? = null
    var stationBrand: String? = null
    var stationAddress: String? = null
    var stationLatLng: LatLng? = null
    var stationPetrolTypeIDs: ArrayList<String> = ArrayList()
    var distanceFromUser: Float? = null

    constructor(parcel: Parcel) : this() {
        stationID = parcel.readString()
        stationName = parcel.readString()
        stationBrand = parcel.readString()
        stationAddress = parcel.readString()
        stationLatLng = parcel.readParcelable(LatLng::class.java.classLoader)
    }

    constructor(stationID : String, stationName : String, stationBrand : String, stationAddress : String, stationLatLng : LatLng) : this() {
        this.stationID = stationID
        this.stationName = stationName
        this.stationBrand = stationBrand
        this.stationAddress = stationAddress
        this.stationLatLng = stationLatLng
    }

    constructor(jsonObject: JSONObject) : this()
    {
        stationID = jsonObject.optString("station_id")
        stationName = jsonObject.optString("station_name")
        stationBrand = jsonObject.optString("station_brand")
        stationAddress = jsonObject.optString("station_address")
        stationLatLng = LatLng(jsonObject.optDouble("station_latitude"), jsonObject.optDouble("station_longitude"))

        val petrolIDJsonArray = jsonObject.optJSONArray("station_petrol_types")
        for (pID in 0 until petrolIDJsonArray.length()) {
            stationPetrolTypeIDs.add(petrolIDJsonArray.getString(pID))
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stationID)
        parcel.writeString(stationName)
        parcel.writeString(stationBrand)
        parcel.writeString(stationAddress)
        parcel.writeParcelable(stationLatLng, flags)
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