package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class User() : Parcelable {
    var userID: String? = null
    var username: String? = null
    var password: String? = null
    var preferredPetrolType: PetrolType? = null
    var preferredBrands: ArrayList<Brand> = ArrayList()

    constructor(parcel: Parcel) : this() {
        userID = parcel.readString()
        username = parcel.readString()
        password = parcel.readString()
        preferredPetrolType = parcel.readParcelable(PetrolType::class.java.classLoader)
        preferredBrands = parcel.readArrayList(Brand::class.java.classLoader) as ArrayList<Brand>
    }

    constructor(jsonObject: JSONObject) : this() {
        userID = jsonObject.optString("userID")
        username = jsonObject.optString("Username")
        password = jsonObject.optString("Password")
    }

    constructor(userID: String, username: String, password: String,
                petrolType: PetrolType, prefBrands : ArrayList<Brand>) : this(){
        this.userID = userID
        this.username = username
        this.password = password
        this.preferredPetrolType = petrolType
        this.preferredBrands = prefBrands
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeParcelable(preferredPetrolType, flags)
        parcel.writeList(preferredBrands)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}