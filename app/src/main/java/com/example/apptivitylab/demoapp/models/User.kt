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
        this.userID = parcel.readString()
        this.username = parcel.readString()
        this.password = parcel.readString()
        this.preferredPetrolType = parcel.readParcelable(PetrolType::class.java.classLoader)
        this.preferredBrands = parcel.readArrayList(Brand::class.java.classLoader) as ArrayList<Brand>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.userID = jsonObject.optString("user_id")
        this.username = jsonObject.optString("user_name")
        this.password = jsonObject.optString("password")

        val petrolType = jsonObject.optJSONObject("preferred_petrol_type")
        if (petrolType != null) {
            this.preferredPetrolType = PetrolType(petrolType)
        }
//        jsonObject.optJSONObject("preferred_petrol_type")?.let {
//            this.preferredPetrolType = PetrolType(it)
//        }

        val preferredBrandsJSONArray = jsonObject.optJSONArray("preferred_brands")
        for (brand in 0 until preferredBrandsJSONArray.length()) {
            this.preferredBrands.add(Brand(preferredBrandsJSONArray.getJSONObject(brand)))
        }
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