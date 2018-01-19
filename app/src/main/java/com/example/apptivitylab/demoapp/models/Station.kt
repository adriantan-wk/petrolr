package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class Station() : Parcelable{
    var stationID : String? = null
    var stationName : String? = null
    var stationBrand : String? = null
    var stationLatLng : LatLng? = null
    var distanceFromUser : Float? = null
    var stationPetrolTypes : ArrayList<Petrol> = ArrayList()

    constructor(parcel: Parcel) : this() {
        stationID = parcel.readString()
        stationName = parcel.readString()
        stationBrand = parcel.readString()
        stationLatLng = parcel.readParcelable(LatLng::class.java.classLoader)
    }

    constructor(stationID : String, stationName : String, stationBrand : String, stationLatLng : LatLng) : this() {
        this.stationID = stationID
        this.stationName = stationName
        this.stationBrand = stationBrand
        this.stationLatLng = stationLatLng
    }

    constructor(jsonObject: JSONObject) : this()
    {
        stationID = jsonObject.optString("ID:")
        stationName = jsonObject.optString("Name:")
        stationBrand = jsonObject.optString("Brand")
        stationLatLng = LatLng(jsonObject.optDouble("Latitude"), jsonObject.optDouble("Longitude"))
        //stationPetrolTypes = jsonObject.opt("Petrol")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stationID)
        parcel.writeString(stationName)
        parcel.writeString(stationBrand)
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